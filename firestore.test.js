const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_azkari_123";
const BOB_UID = "bob_azkari_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any Azkari collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("settings").doc("main").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("customAthkar").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("dailyLogs").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("reminders").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("settings").doc("main").set({
      userId: ALICE_UID,
      languageCode: "ar",
      themeMode: "system",
      fontScale: "medium",
      hapticEnabled: true,
      favoriteIds: [],
      tasbihTarget: 33,
      tasbihPhraseId: "subhanallah",
      tasbihCurrentCount: 0,
      tasbihTotalLifetime: 0,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("UserSettings: owner create, partial update, cross-user denial, and ghost field denial", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);
  const settingsRef = aliceDb.collection("users").doc(ALICE_UID).collection("settings").doc("main");

  await assertSucceeds(
    settingsRef.set({
      userId: ALICE_UID,
      languageCode: "ar",
      themeMode: "dark",
      fontScale: "large",
      hapticEnabled: true,
      favoriteIds: ["morning_1", "evening_1"],
      tasbihTarget: 33,
      tasbihPhraseId: "subhanallah",
      tasbihCurrentCount: 12,
      tasbihTotalLifetime: 120,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Valid partial update
  await assertSucceeds(
    settingsRef.update({
      languageCode: "fr",
      tasbihCurrentCount: 13,
      tasbihTotalLifetime: 121,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject ghost field
  await assertFails(
    settingsRef.update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject invalid language
  await assertFails(
    settingsRef.update({
      languageCode: "de",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt modification
  await assertFails(
    settingsRef.update({
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject cross-user read & write
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("settings").doc("main").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("settings").doc("main").update({
      themeMode: "light",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("CustomAthkar: owner CRUD, list query, and validation bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);
  const col = aliceDb.collection("users").doc(ALICE_UID).collection("customAthkar");

  // Empty list query succeeds for owner
  await assertSucceeds(col.get());

  // Valid create (no @DocumentId "id" key)
  await assertSucceeds(
    col.doc("custom_1").set({
      userId: ALICE_UID,
      title: "دعاء الوالدين",
      arabicText: "رب ارحمهما كما ربياني صغيرا",
      translation: "My Lord, have mercy upon them as they brought me up when I was small.",
      category: "custom",
      targetCount: 3,
      favorited: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Valid partial update
  await assertSucceeds(
    col.doc("custom_1").update({
      favorited: false,
      targetCount: 7,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject extra @DocumentId "id" field on create
  await assertFails(
    col.doc("custom_2").set({
      id: "custom_2",
      userId: ALICE_UID,
      title: "Invalid",
      arabicText: "Test",
      translation: "",
      category: "custom",
      targetCount: 1,
      favorited: false,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Reject future timestamp
  await assertFails(
    col.doc("custom_3").set({
      userId: ALICE_UID,
      title: "Future",
      arabicText: "Test",
      translation: "",
      category: "custom",
      targetCount: 1,
      favorited: false,
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Cross-user list denied
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("customAthkar").get());

  // Owner delete succeeds
  await assertSucceeds(col.doc("custom_1").delete());
});

test("DailyLogs & Reminders: owner CRUD and strict field validation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  const logRef = aliceDb.collection("users").doc(ALICE_UID).collection("dailyLogs").doc("2026-11-19");
  await assertSucceeds(
    logRef.set({
      userId: ALICE_UID,
      dateKey: "2026-11-19",
      athkarCompletedCount: 5,
      tasbihCount: 33,
      completedDhikrIds: ["morning_1"],
      completedCategories: ["morning"],
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    logRef.update({
      athkarCompletedCount: 6,
      tasbihCount: 66,
      completedDhikrIds: ["morning_1", "morning_2"],
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject mutating dateKey
  await assertFails(
    logRef.update({
      dateKey: "2026-11-20",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  const remRef = aliceDb.collection("users").doc(ALICE_UID).collection("reminders").doc("rem_morning");
  await assertSucceeds(
    remRef.set({
      userId: ALICE_UID,
      title: "أذكار الصباح",
      categoryId: "morning",
      hour: 6,
      minute: 30,
      enabled: true,
      repeatDays: [1, 2, 3, 4, 5, 6, 7],
      createdAt: created,
      updatedAt: created,
    })
  );

  // Reject invalid hour (25)
  await assertFails(
    remRef.update({
      hour: 25,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(remRef.delete());
});
