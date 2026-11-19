package com.example.data.catalog

import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.TasbihPhrase

object BuiltInAthkarCatalog {

    val tasbihPhrases: List<TasbihPhrase> = listOf(
        TasbihPhrase(
            id = "subhanallah",
            arabic = "سُبْحَانَ اللَّهِ",
            transliteration = "SubhanAllah",
            translationEn = "Glory be to Allah",
            translationFr = "Gloire à Allah",
            defaultTarget = 33
        ),
        TasbihPhrase(
            id = "alhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            translationEn = "All praise is due to Allah",
            translationFr = "Louange à Allah",
            defaultTarget = 33
        ),
        TasbihPhrase(
            id = "allahu_akbar",
            arabic = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            translationEn = "Allah is the Greatest",
            translationFr = "Allah est le Plus Grand",
            defaultTarget = 34
        ),
        TasbihPhrase(
            id = "astaghfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ",
            transliteration = "Astaghfirullah al-Azim",
            translationEn = "I seek forgiveness from Allah the Almighty",
            translationFr = "Je demande pardon à Allah l'Immense",
            defaultTarget = 100
        ),
        TasbihPhrase(
            id = "subhanallah_wabihamdihi",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahil-Azim",
            translationEn = "Glory and praise be to Allah, Glory be to Allah the Supreme",
            translationFr = "Gloire et louange à Allah, Gloire à Allah l'Immense",
            defaultTarget = 100
        ),
        TasbihPhrase(
            id = "salawat",
            arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
            transliteration = "Allahumma salli wa sallim 'ala nabiyyina Muhammad",
            translationEn = "O Allah, send peace and blessings upon our Prophet Muhammad",
            translationFr = "Ô Allah, prie et salue notre Prophète Muhammad",
            defaultTarget = 10
        ),
        TasbihPhrase(
            id = "hawqala",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            translationEn = "There is no power nor strength except through Allah",
            translationFr = "Il n'y a de force ni de puissance qu'en Allah",
            defaultTarget = 33
        )
    )

    val builtInAthkar: List<DhikrItem> = listOf(
        // --- MORNING ---
        DhikrItem(
            id = "morning_kursi",
            category = DhikrCategory.MORNING,
            titleAr = "آية الكرسي",
            titleEn = "Ayat al-Kursi",
            titleFr = "Verset du Trône",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum...",
            translationEn = "Allah — there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep...",
            translationFr = "Allah ! Point de divinité à part Lui, le Vivant, Celui qui subsiste par Lui-même. Ni somnolence ni sommeil ne Le saisissent...",
            virtueAr = "من قالها حين يصبح أُجير من الجن حتى يمسي.",
            virtueEn = "Whoever recites it in the morning is protected until evening.",
            virtueFr = "Quiconque le récite le matin est protégé jusqu'au soir.",
            targetCount = 1
        ),
        DhikrItem(
            id = "morning_asbahna",
            category = DhikrCategory.MORNING,
            titleAr = "أصبحنا وأصبح الملك لله",
            titleEn = "Entering the Morning",
            titleFr = "Entrée dans la matinée",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ.",
            transliteration = "Asbahna wa asbahal-mulku lillah, wal-hamdu lillah...",
            translationEn = "We have reached the morning and at this very time all sovereignty belongs to Allah, and all praise is for Allah. None has the right to be worshipped except Allah alone.",
            translationFr = "Nous voici au matin et la royauté appartient à Allah, et louange à Allah. Il n'y a de divinité digne d'adoration qu'Allah Seul.",
            virtueAr = "من أذكار الصباح الثابتة في صحيح مسلم.",
            virtueEn = "Authentic morning remembrance (Sahih Muslim).",
            virtueFr = "Rappel authentique du matin (Sahih Muslim).",
            targetCount = 1
        ),
        DhikrItem(
            id = "morning_sayyid_istighfar",
            category = DhikrCategory.MORNING,
            titleAr = "سيد الاستغفار",
            titleEn = "Master Supplication for Forgiveness",
            titleFr = "Maîtresse demande de pardon",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka...",
            translationEn = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your servant... Forgive me, for none forgives sins except You.",
            translationFr = "Ô Allah, Tu es mon Seigneur, nulle divinité à part Toi. Tu m'as créé et je suis Ton serviteur... Pardonne-moi, car nul ne pardonne les péchés sauf Toi.",
            virtueAr = "من قالها موقناً بها حين يصبح ومات من يومه دخل الجنة.",
            virtueEn = "Whoever recites it with conviction in the morning enters Paradise if they pass away that day.",
            virtueFr = "Quiconque la récite avec certitude le matin entrera au Paradis s'il décède ce jour-là.",
            targetCount = 1
        ),
        DhikrItem(
            id = "morning_bismillah",
            category = DhikrCategory.MORNING,
            titleAr = "بسم الله الذي لا يضر مع اسمه شيء",
            titleEn = "Protection in the Name of Allah",
            titleFr = "Protection au nom d'Allah",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
            transliteration = "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
            translationEn = "In the Name of Allah, with Whose Name nothing on earth or in heaven can cause harm, and He is the All-Hearing, the All-Knowing.",
            translationFr = "Au nom d'Allah, avec le nom Duquel rien sur terre ni dans le ciel ne peut nuire, et Il est l'Audient, l'Omniscient.",
            virtueAr = "من قالها ثلاثاً لم يضره شيء.",
            virtueEn = "Recite 3 times — nothing shall harm you.",
            virtueFr = "À réciter 3 fois — rien ne pourra vous nuire.",
            targetCount = 3
        ),
        DhikrItem(
            id = "morning_raditu",
            category = DhikrCategory.MORNING,
            titleAr = "رضيت بالله رباً",
            titleEn = "Contentment with Allah",
            titleFr = "Satisfaction d'Allah",
            arabicText = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا.",
            transliteration = "Raditu billahi Rabban, wa bil-Islami dinan, wa bi-Muhammadin nabiyya.",
            translationEn = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (peace be upon him) as my Prophet.",
            translationFr = "Je suis satisfait d'Allah comme Seigneur, de l'Islam comme religion et de Muhammad (paix sur lui) comme Prophète.",
            virtueAr = "كان حقاً على الله أن يرضيه يوم القيامة.",
            virtueEn = "Allah has promised to please whoever recites this 3 times morning and evening.",
            virtueFr = "Allah a promis de satisfaire quiconque le récite 3 fois matin et soir.",
            targetCount = 3
        ),

        // --- EVENING ---
        DhikrItem(
            id = "evening_amsayna",
            category = DhikrCategory.EVENING,
            titleAr = "أمسينا وأمسى الملك لله",
            titleEn = "Entering the Evening",
            titleFr = "Entrée dans la soirée",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا.",
            transliteration = "Amsayna wa amsal-mulku lillah, wal-hamdu lillah...",
            translationEn = "We have reached the evening and at this very time all sovereignty belongs to Allah, and all praise is for Allah. O Lord, I ask You for the good of this night and what follows it.",
            translationFr = "Nous voici au soir et la royauté appartient à Allah. Seigneur, je Te demande le bien de cette nuit et de ce qui la suit.",
            virtueAr = "من أذكار المساء الثابتة في صحيح مسلم.",
            virtueEn = "Authentic evening remembrance (Sahih Muslim).",
            virtueFr = "Rappel authentique du soir (Sahih Muslim).",
            targetCount = 1
        ),
        DhikrItem(
            id = "evening_kalimat",
            category = DhikrCategory.EVENING,
            titleAr = "أعوذ بكلمات الله التامات",
            titleEn = "Refuge in the Perfect Words of Allah",
            titleFr = "Refuge dans les paroles parfaites d'Allah",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
            transliteration = "A'udhu bi-kalimatillahit-tammati min sharri ma khalaq.",
            translationEn = "I seek refuge in the Perfect Words of Allah from the evil of what He has created.",
            translationFr = "Je cherche refuge auprès des paroles parfaites d'Allah contre le mal de ce qu'Il a créé.",
            virtueAr = "من قالها حين يمسي ثلاث مرات لم تضره حمة تلك الليلة.",
            virtueEn = "Recite 3 times in the evening for protection throughout the night.",
            virtueFr = "À réciter 3 fois le soir pour être protégé toute la nuit.",
            targetCount = 3
        ),
        DhikrItem(
            id = "evening_hasbi",
            category = DhikrCategory.EVENING,
            titleAr = "حسبي الله لا إله إلا هو",
            titleEn = "Allah is Sufficient for Me",
            titleFr = "Allah me suffit",
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ.",
            transliteration = "Hasbiyallahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azim.",
            translationEn = "Allah is sufficient for me; there is no deity except Him. Upon Him I have relied, and He is the Lord of the Great Throne.",
            translationFr = "Allah me suffit : point de divinité à part Lui. En Lui je place ma confiance, et Il est le Seigneur du Trône immense.",
            virtueAr = "من قالها سبع مرات كفاه الله ما أهمه من أمر الدنيا والآخرة.",
            virtueEn = "Recite 7 times — Allah will suffice you against all worldly and hereafter worries.",
            virtueFr = "À réciter 7 fois — Allah vous suffira face à tous vos soucis.",
            targetCount = 7
        ),
        DhikrItem(
            id = "evening_hayyu_qayyum",
            category = DhikrCategory.EVENING,
            titleAr = "يا حي يا قيوم برحمتك أستغيث",
            titleEn = "Seeking Mercy of the Ever-Living",
            titleFr = "Appel à la miséricorde du Vivant",
            arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
            transliteration = "Ya Hayyu ya Qayyumu bi-rahmatika astaghith, aslih li sha'ni kullahu...",
            translationEn = "O Ever-Living, O Sustainer, in Your mercy I seek relief; rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
            translationFr = "Ô Vivant, Ô Subsistant par Toi-même, j'implore secours auprès de Ta miséricorde ; améliore toute ma situation et ne me laisse pas à moi-même ne serait-ce qu'un clin d'œil.",
            virtueAr = "وصية النبي ﷺ لابنته فاطمة رضي الله عنها.",
            virtueEn = "Recommended by the Prophet (peace be upon him) morning and evening.",
            virtueFr = "Recommandé par le Prophète (paix sur lui) matin et soir.",
            targetCount = 1
        ),

        // --- AFTER PRAYER ---
        DhikrItem(
            id = "prayer_istighfar",
            category = DhikrCategory.AFTER_PRAYER,
            titleAr = "الاستغفار والسلام بعد الصلاة",
            titleEn = "Seeking Forgiveness & Peace",
            titleFr = "Demande de pardon et paix",
            arabicText = "أَسْتَغْفِرُ اللَّهَ (ثلاثاً)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ.",
            transliteration = "Astaghfirullah (x3). Allahumma Antas-Salamu wa minkas-salam, tabarakta ya Dhal-Jalali wal-Ikram.",
            translationEn = "I seek the forgiveness of Allah (3 times). O Allah, You are Peace and from You comes peace. Blessed are You, O Possessor of majesty and honor.",
            translationFr = "Je demande pardon à Allah (3 fois). Ô Allah, Tu es la Paix et de Toi vient la paix. Béni sois-Tu, ô Détenteur de majesté et de générosité.",
            virtueAr = "كان النبي ﷺ إذا انصرف من صلاته استغفر ثلاثاً وقال هذا الذكر.",
            virtueEn = "Recited immediately after concluding the obligatory prayer.",
            virtueFr = "Récité immédiatement après la prière obligatoire.",
            targetCount = 3
        ),
        DhikrItem(
            id = "prayer_tasbih_33",
            category = DhikrCategory.AFTER_PRAYER,
            titleAr = "تسبيح ما بعد الصلاة",
            titleEn = "Glorification After Prayer",
            titleFr = "Glorification après la prière",
            arabicText = "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَاللَّهُ أَكْبَرُ.",
            transliteration = "SubhanAllah, Alhamdulillah, Allahu Akbar.",
            translationEn = "Glory be to Allah, all praise is due to Allah, and Allah is the Greatest (33 times each).",
            translationFr = "Gloire à Allah, louange à Allah, et Allah est le Plus Grand (33 fois chacun).",
            virtueAr = "غُفرت خطاياه وإن كانت مثل زبد البحر.",
            virtueEn = "Sins are forgiven even if they are like the foam of the sea.",
            virtueFr = "Les péchés sont pardonnés même s'ils sont comme l'écume de la mer.",
            targetCount = 33
        ),
        DhikrItem(
            id = "prayer_ainni",
            category = DhikrCategory.AFTER_PRAYER,
            titleAr = "اللهم أعني على ذكرك",
            titleEn = "Help Me Remember You",
            titleFr = "Aide-moi à T'évoquer",
            arabicText = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ، وَشُكْرِكَ، وَحُسْنِ عِبَادَتِكَ.",
            transliteration = "Allahumma a'inni 'ala dhikrika wa shukrika wa husni 'ibadatika.",
            translationEn = "O Allah, help me to remember You, to give thanks to You, and to worship You in the best of manners.",
            translationFr = "Ô Allah, aide-moi à T'évoquer, à Te remercier et à T'adorer de la meilleure manière.",
            virtueAr = "أوصى النبي ﷺ معاذ بن جبل ألا يدعها دبر كل صلاة.",
            virtueEn = "Advised by the Prophet (peace be upon him) to be recited after every prayer.",
            virtueFr = "Conseillé par le Prophète (paix sur lui) après chaque prière.",
            targetCount = 1
        ),

        // --- SLEEP ---
        DhikrItem(
            id = "sleep_bismika",
            category = DhikrCategory.SLEEP,
            titleAr = "باسمك اللهم أموت وأحيا",
            titleEn = "In Your Name I Sleep",
            titleFr = "En Ton nom je m'endors",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا.",
            transliteration = "Bismika Allahumma amutu wa ahya.",
            translationEn = "In Your Name, O Allah, I die and I live.",
            translationFr = "En Ton nom, ô Allah, je meurs et je vis.",
            virtueAr = "كان النبي ﷺ إذا أخذ مضجعه قال هذا الذكر.",
            virtueEn = "Recited when lying down to sleep.",
            virtueFr = "Récité au moment de se coucher.",
            targetCount = 1
        ),
        DhikrItem(
            id = "sleep_ikhlas_muawwidhat",
            category = DhikrCategory.SLEEP,
            titleAr = "سورة الإخلاص والمعوذتين",
            titleEn = "Surah Al-Ikhlas & Protection Surahs",
            titleFr = "Sourate Al-Ikhlas et les Protectrices",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ.",
            transliteration = "Qul Huwallahu Ahad, Allahus-Samad, lam yalid wa lam yulad, wa lam yakun lahu kufuwan ahad.",
            translationEn = "Say, He is Allah, the One; Allah, the Eternal Refuge. He neither begets nor is born, nor is there to Him any equivalent.",
            translationFr = "Dis : Il est Allah, Unique. Allah, le Seul à être imploré. Il n'a jamais engendré, n'a pas été engendré non plus, et nul n'est égal à Lui.",
            virtueAr = "تُقرأ مع الفلق والناس ثلاث مرات قبل النوم.",
            virtueEn = "Recite 3 times along with Al-Falaq and An-Nas before sleep.",
            virtueFr = "À réciter 3 fois avec Al-Falaq et An-Nas avant de dormir.",
            targetCount = 3
        ),

        // --- WAKING UP ---
        DhikrItem(
            id = "wake_alhamdulillah",
            category = DhikrCategory.WAKING_UP,
            titleAr = "الحمد لله الذي أحيانا",
            titleEn = "Praise Upon Waking",
            titleFr = "Louange au réveil",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ.",
            transliteration = "Alhamdulillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur.",
            translationEn = "All praise is for Allah who gave us life after having taken it from us, and unto Him is the resurrection.",
            translationFr = "Louange à Allah qui nous a rendu la vie après nous avoir fait mourir, et vers Lui est la résurrection.",
            virtueAr = "أول ما يُقال عند الاستيقاظ من النوم.",
            virtueEn = "The first remembrance upon waking from sleep.",
            virtueFr = "Premier rappel à prononcer au réveil.",
            targetCount = 1
        ),

        // --- GENERAL ---
        DhikrItem(
            id = "general_hasana",
            category = DhikrCategory.GENERAL,
            titleAr = "ربنا آتنا في الدنيا حسنة",
            titleEn = "Good in This World and the Hereafter",
            titleFr = "Le bien ici-bas et dans l'au-delà",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ.",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
            translationEn = "Our Lord, grant us good in this world and good in the Hereafter, and protect us from the punishment of the Fire.",
            translationFr = "Seigneur ! Accorde-nous belle part ici-bas, et belle part aussi dans l'au-delà ; et protège-nous du châtiment du Feu.",
            virtueAr = "من أكثر أدعية النبي ﷺ وجمع فيها خيري الدنيا والآخرة.",
            virtueEn = "One of the most frequent comprehensive supplications of the Prophet (peace be upon him).",
            virtueFr = "L'une des invocations les plus fréquentes du Prophète (paix sur lui).",
            targetCount = 3
        ),
        DhikrItem(
            id = "general_yunus",
            category = DhikrCategory.GENERAL,
            titleAr = "دعاء ذي النون",
            titleEn = "Supplication of Prophet Yunus",
            titleFr = "Invocation du Prophète Younous",
            arabicText = "لَا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ.",
            transliteration = "La ilaha illa Anta, Subhanaka inni kuntu minaz-zalimin.",
            translationEn = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            translationFr = "Pas de divinité à part Toi ! Pureté à Toi ! J'ai été vraiment du nombre des injustes.",
            virtueAr = "ما دعا بها مسلم في شيء قط إلا استجاب الله له.",
            virtueEn = "No Muslim supplicates with it for anything except that Allah responds to them.",
            virtueFr = "Aucun musulman n'invoque par ces mots sans qu'Allah ne lui réponde.",
            targetCount = 7
        )
    )
}
