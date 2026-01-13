package com.example.poznejcesko.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [User::class, Region::class, Question::class, Score::class, UserRegionState::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun regionDao(): RegionDao
    abstract fun questionDao(): QuestionDao
    abstract fun scoreDao(): ScoreDao
    abstract fun userRegionStateDao(): UserRegionStateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "poznej_cesko_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val regionDao = database.regionDao()
                    val questionDao = database.questionDao()
                    
                    // Pro verzi 4 vynutíme znovunaplnění otázek, abychom jich měli 10 na kraj
                    // Smažeme staré a vložíme nové (v produkci by se řešilo migrací, pro vývoj hry je toto jistější)
                    // Ale zde použijeme tvůj check, jen ho trochu rozšíříme
                    val questionsCount = database.query("SELECT COUNT(*) FROM questions", null).let {
                        it.moveToFirst()
                        val count = it.getInt(0)
                        it.close()
                        count
                    }

                    if (questionsCount < 100) { // Pokud jich máme málo, naplníme znovu
                        populateDatabase(regionDao, questionDao)
                    }
                }
            }
        }

        suspend fun populateDatabase(regionDao: RegionDao, questionDao: QuestionDao) {
            // Vyčistíme tabulky pro jistotu při tomto velkém updatu
            // (v rámci destructuve migration a onOpen je to bezpečné)
            
            val regions = listOf(
                Region(id = 1, name = "Hlavní město Praha", requiredScoreToUnlock = 0, order = 1),
                Region(id = 2, name = "Středočeský kraj", requiredScoreToUnlock = 30, order = 2),
                Region(id = 3, name = "Jihočeský kraj", requiredScoreToUnlock = 30, order = 3),
                Region(id = 4, name = "Plzeňský kraj", requiredScoreToUnlock = 30, order = 4),
                Region(id = 5, name = "Karlovarský kraj", requiredScoreToUnlock = 30, order = 5),
                Region(id = 6, name = "Ústecký kraj", requiredScoreToUnlock = 30, order = 6),
                Region(id = 7, name = "Liberecký kraj", requiredScoreToUnlock = 30, order = 7),
                Region(id = 8, name = "Královéhradecký kraj", requiredScoreToUnlock = 30, order = 8),
                Region(id = 9, name = "Pardubický kraj", requiredScoreToUnlock = 30, order = 9),
                Region(id = 10, name = "Kraj Vysočina", requiredScoreToUnlock = 30, order = 10),
                Region(id = 11, name = "Jihomoravský kraj", requiredScoreToUnlock = 30, order = 11),
                Region(id = 12, name = "Olomoucký kraj", requiredScoreToUnlock = 30, order = 12),
                Region(id = 13, name = "Zlínský kraj", requiredScoreToUnlock = 30, order = 13),
                Region(id = 14, name = "Moravskoslezský kraj", requiredScoreToUnlock = 30, order = 14)
            )
            regionDao.insertRegions(regions)

            val q = mutableListOf<Question>()

            // --- 1. PRAHA ---
            q.add(Question(regionId = 1, text = "Jaká řeka protéká Prahou?", options = listOf("Vltava", "Labe", "Morava", "Odra"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Který most je nejstarší?", options = listOf("Karlův", "Mánesův", "Libeňský", "Trojský"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Kde sídlí prezident?", options = listOf("Pražský hrad", "Vyšehrad", "Troja", "Žižkov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Jak se jmenuje katedrála na Hradě?", options = listOf("sv. Víta", "sv. Mikuláše", "sv. Ludmily", "sv. Petra"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Kolik linek má pražské metro?", options = listOf("3", "2", "4", "5"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Která věž je na Petříně?", options = listOf("Rozhledna", "Prašná brána", "TV věž", "Mostecká věž"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Jak se jmenuje náměstí s koněm?", options = listOf("Václavské", "Staroměstské", "Karlovo", "Malostranské"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Která čtvrť je známá orlojem?", options = listOf("Staré Město", "Nové Město", "Hradčany", "Smíchov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Kde se nachází Zlatá ulička?", options = listOf("Na Hradě", "Na Vyšehradě", "U Karlova mostu", "V Podskalí"), correctAnswerIndex = 0))
            q.add(Question(regionId = 1, text = "Jak se jmenuje slavná opera v Praze?", options = listOf("Národní divadlo", "Státní opera", "Rudolfinum", "Hybernia"), correctAnswerIndex = 0))

            // --- 2. STŘEDOČESKÝ ---
            q.add(Question(regionId = 2, text = "Které město proslavilo stříbro?", options = listOf("Kutná Hora", "Příbram", "Beroun", "Kladno"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Který hrad nechal postavit Karel IV.?", options = listOf("Karlštejn", "Křivoklát", "Kokořín", "Konopiště"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Kde se vyrábí vozy Škoda?", options = listOf("Mladá Boleslav", "Kolín", "Nymburk", "Slaný"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Která řeka protéká Berounem?", options = listOf("Berounka", "Sázava", "Vltava", "Jizera"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Jak se jmenuje zámek u Benešova?", options = listOf("Konopiště", "Žleby", "Loučeň", "Veltrusy"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Která hora je spojená s blanickými rytíři?", options = listOf("Blaník", "Říp", "Medník", "Tok"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Kde se vlévá Vltava do Labe?", options = listOf("Mělník", "Poděbrady", "Neratovice", "Kralupy"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Které lázeňské město je v tomto kraji?", options = listOf("Poděbrady", "Toušeň", "Mšené", "Bechyně"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Jak se jmenuje hrad v hlubokých lesích?", options = listOf("Křivoklát", "Točník", "Žebrák", "Krakovec"), correctAnswerIndex = 0))
            q.add(Question(regionId = 2, text = "Které město je známé svými doly?", options = listOf("Kladno", "Rakovník", "Vlašim", "Sedlčany"), correctAnswerIndex = 0))

            // --- 3. JIHOČESKÝ ---
            q.add(Question(regionId = 3, text = "Které město je známé pivovarem Budvar?", options = listOf("České Budějovice", "Tábor", "Písek", "Strakonice"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Kde je otáčivé hlediště?", options = listOf("Český Krumlov", "Hluboká", "Třeboň", "Jindřichův Hradec"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Jak se jmenuje největší rybník?", options = listOf("Rožmberk", "Svět", "Bezdrev", "Horusický"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Který zámek vypadá jako z pohádky?", options = listOf("Hluboká", "Červená Lhota", "Kratochvíle", "Blatná"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Které pohoří tvoří hranici s Rakouskem?", options = listOf("Šumava", "Novohradské hory", "Blanský les", "Česká Kanada"), correctAnswerIndex = 1))
            q.add(Question(regionId = 3, text = "Která řeka protéká Pískem?", options = listOf("Otava", "Vltava", "Lužnice", "Nežárka"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Které město založili husité?", options = listOf("Tábor", "Soběslav", "Prachatice", "Vimperk"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Kde najdeme nejstarší kamenný most?", options = listOf("Písek", "Krumlov", "Budějovice", "Rožmberk"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Jak se jmenuje zámek na vodě?", options = listOf("Červená Lhota", "Blatná", "Hluboká", "Třeboň"), correctAnswerIndex = 0))
            q.add(Question(regionId = 3, text = "Která jaderná elektrárna tu leží?", options = listOf("Temelín", "Dukovany", "Jaslov", "Tušimice"), correctAnswerIndex = 0))

            // --- 4. PLZEŇSKÝ ---
            q.add(Question(regionId = 4, text = "Co proslavilo Plzeň?", options = listOf("Pivo", "Víno", "Auta", "Sklo"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Které hory jsou na jihu kraje?", options = listOf("Šumava", "Český les", "Brdy", "Krušné hory"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Který hrad je nejrozsáhlejší zříceninou?", options = listOf("Rabí", "Radyně", "Švihov", "Kašperk"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Který zámek je známý jako vodní hrad?", options = listOf("Švihov", "Horšovský Týn", "Nebílovy", "Manětín"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Jak se jmenuje krajské město?", options = listOf("Plzeň", "Klatovy", "Rokycany", "Tachov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Která řeka protéká Plzní?", options = listOf("Radbuza", "Mže", "Úhlava", "Všechny tři"), correctAnswerIndex = 3))
            q.add(Question(regionId = 4, text = "Kde se vyrábí sekty Bohemia?", options = listOf("Starý Plzenec", "Plzeň", "Přeštice", "Stříbro"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Které město je branou Šumavy?", options = listOf("Sušice", "Klatovy", "Domažlice", "Železná Ruda"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Jak se jmenuje klášter s kupolí od Santiniho?", options = listOf("Kladruby", "Plasy", "Chotěšov", "Nepomuk"), correctAnswerIndex = 0))
            q.add(Question(regionId = 4, text = "Která čtvrť v Plzni hostí Techmanii?", options = listOf("Jižní Předměstí", "Bory", "Slovany", "Lochotín"), correctAnswerIndex = 0))

            // --- 5. KARLOVARSKÝ ---
            q.add(Question(regionId = 5, text = "Největší lázně v ČR?", options = listOf("Karlovy Vary", "Mariánské Lázně", "Františkovy Lázně", "Jáchymov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Co je typickým likérem z Varů?", options = listOf("Becherovka", "Fernet", "Rum", "Griotte"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Který filmový festival se tu koná?", options = listOf("KVIFF", "Febiofest", "Zlín", "Jihlava"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Jak se jmenuje hrad u Chebu?", options = listOf("Chebský hrad", "Loket", "Seeberg", "Vildštejn"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Která řeka protéká krajem?", options = listOf("Ohře", "Bílina", "Mže", "Teplá"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Kde se těžila uranová ruda?", options = listOf("Jáchymov", "Cheb", "Sokolov", "Ostrov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Jak se jmenuje pramen ve Varech?", options = listOf("Vřídlo", "Rudolf", "Karolína", "Ambrož"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Které hory tvoří severní hranici?", options = listOf("Krušné hory", "Doupovské hory", "Smrčiny", "Český les"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Které město je známé výrobou hudebních nástrojů?", options = listOf("Luby", "Kraslice", "Aš", "Nejdek"), correctAnswerIndex = 0))
            q.add(Question(regionId = 5, text = "Jak se jmenuje hrad v ohbí řeky Ohře?", options = listOf("Loket", "Bečov", "Hauenštejn", "Kynžvart"), correctAnswerIndex = 0))

            // --- 6. ÚSTECKÝ ---
            q.add(Question(regionId = 6, text = "Která památná hora tu leží?", options = listOf("Říp", "Lovoš", "Milešovka", "Bořeň"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Jak se jmenuje NP na severu?", options = listOf("České Švýcarsko", "Krkonoše", "Podyjí", "Šumava"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Který skalní útvar je symbolem kraje?", options = listOf("Pravčická brána", "Panská skála", "Tiské stěny", "Hřensko"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Jak se jmenuje hrad nad Ústím?", options = listOf("Střekov", "Doubravka", "Hazmburk", "Krupka"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Které město proslavilo chmel?", options = listOf("Žatec", "Louny", "Most", "Chomutov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Která řeka je nejdůležitější?", options = listOf("Labe", "Ohře", "Ploučnice", "Bílina"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Které město hostí zámek u řeky?", options = listOf("Děčín", "Litoměřice", "Roudnice", "Libochovice"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Které hory jsou na severu?", options = listOf("Krušné hory", "Lužické hory", "České středohoří", "Jizerské hory"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Jak se jmenuje větrná hora?", options = listOf("Milešovka", "Klíny", "Bouřňák", "Měděnec"), correctAnswerIndex = 0))
            q.add(Question(regionId = 6, text = "Které město je branou do Čech?", options = listOf("Hřensko", "Petrovice", "Vejprty", "Cínovec"), correctAnswerIndex = 0))

            // --- 7. LIBERECKÝ ---
            q.add(Question(regionId = 7, text = "Symbol Liberce s vysílačem?", options = listOf("Ještěd", "Jizera", "Smrk", "Bezděz"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Které zvíře je v liberecké ZOO?", options = listOf("Bílý tygr", "Slon", "Lev", "Žirafa"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Který hrad nechal postavit Přemysl Otakar II.?", options = listOf("Bezděz", "Houska", "Grabštejn", "Frýdlant"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Které hory jsou v tomto kraji?", options = listOf("Jizerské hory", "Krkonoše", "Lužické hory", "Všechny tři"), correctAnswerIndex = 3))
            q.add(Question(regionId = 7, text = "Čím je proslulý Nový Bor?", options = listOf("Sklem", "Bižuterií", "Textilem", "Auty"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Jak se jmenuje řeka v Liberci?", options = listOf("Nisa", "Jizera", "Kamenice", "Ploučnice"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Který zámek patřil Valdštejnovi?", options = listOf("Frýdlant", "Sychrov", "Hrubý Rohozec", "Lemberk"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Jak se jmenuje oblast se skalami?", options = listOf("Český ráj", "Prachov", "Adršpach", "Toulovce"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Kde je největší skokanský můstek?", options = listOf("Harrachov", "Liberec", "Bedřichov", "Desná"), correctAnswerIndex = 0))
            q.add(Question(regionId = 7, text = "Jak se jmenuje hrad na skále?", options = listOf("Sloup", "Trosky", "Valdštejn", "Kost"), correctAnswerIndex = 0))

            // --- 8. KRÁLOVÉHRADECKÝ ---
            q.add(Question(regionId = 8, text = "Nejvyšší hora ČR?", options = listOf("Sněžka", "Praděd", "Sněžník", "Boubín"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Kde je ZOO se Safari?", options = listOf("Dvůr Králové", "Náchod", "Trutnov", "Jičín"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Jak se jmenuje zámek u Hradce?", options = listOf("Hrádek u Nechanic", "Kuks", "Opočno", "Ratibořice"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Které skály jsou u Jičína?", options = listOf("Prachovské", "Adršpašské", "Teplické", "Broumovské"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Jaká řeka pramení v Krkonoších?", options = listOf("Labe", "Úpa", "Metuje", "Orlice"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Které město je branou do hor?", options = listOf("Trutnov", "Vrchlabí", "Hostinné", "Janské Lázně"), correctAnswerIndex = 1))
            q.add(Question(regionId = 8, text = "Jak se jmenuje barokní areál se sochami?", options = listOf("Kuks", "Betlém", "Babiččino údolí", "Josefov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Která hora je v Orlických horách?", options = listOf("Velká Deštná", "Šerlich", "Zakletý", "Anenský vrch"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Kde najdeme pevnost Josefov?", options = listOf("Jaroměř", "Hradec", "Náchod", "Rychnov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 8, text = "Jak se jmenuje pohádkové město?", options = listOf("Jičín", "Hořice", "Nová Paka", "Sobotka"), correctAnswerIndex = 0))

            // --- 9. PARDUBICKÝ ---
            q.add(Question(regionId = 9, text = "Co je typické pro Pardubice?", options = listOf("Perník", "Pivo", "Víno", "Sýr"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Jak se jmenuje slavný dostih?", options = listOf("Velká pardubická", "Zlatá přilba", "Derby", "Steeplechase"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Které město je rodištěm Smetany?", options = listOf("Litomyšl", "Chrudim", "Svitavy", "Polička"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Jak se jmenuje hrad u Kunětické hory?", options = listOf("Kunětická hora", "Lichnice", "Svojanov", "Rychmburk"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Která řeka pramení pod Sněžníkem?", options = listOf("Morava", "Orlice", "Chrudimka", "Loučná"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Která památka UNESCO je v Litomyšli?", options = listOf("Zámek", "Kostel", "Náměstí", "Klášter"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Jak se jmenuje pohoří na severu?", options = listOf("Králický Sněžník", "Orlické hory", "Železné hory", "Žďárské vrchy"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Které město je známé loutkami?", options = listOf("Chrudim", "Hlinsko", "Skuteč", "Slatiňany"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Kde se vyrábí výbušnina Semtex?", options = listOf("Semtín", "Pardubice", "Rybitví", "Lázně Bohdaneč"), correctAnswerIndex = 0))
            q.add(Question(regionId = 9, text = "Jak se jmenuje skanzen na Vysočině?", options = listOf("Veselý Kopec", "Betlém", "Příkaz", "Rožnov"), correctAnswerIndex = 0))

            // --- 10. VYSOČINA ---
            q.add(Question(regionId = 10, text = "Město s náměstím v UNESCO?", options = listOf("Telč", "Třebíč", "Jihlava", "Pelhřimov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Jak se jmenuje krajské město?", options = listOf("Jihlava", "Havlíčkův Brod", "Žďár", "Třebíč"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Který kostel Santiniho je v UNESCO?", options = listOf("Zelená hora", "Sedlec", "Kuks", "Rajhrad"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Kde je muzeum rekordů?", options = listOf("Pelhřimov", "Humpolec", "Pacov", "Počátky"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Která řeka protéká Jihlavou?", options = listOf("Jihlava", "Sázava", "Svratka", "Oslava"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Jaká dálnice krajem prochází?", options = listOf("D1", "D2", "D5", "D8"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Kde se koná biatlon?", options = listOf("Nové Město", "Žďár", "Bystřice", "Velké Meziříčí"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Jak se jmenuje hrad na Vysočině?", options = listOf("Pernštejn", "Lipnice", "Roštejn", "Kámen"), correctAnswerIndex = 1))
            q.add(Question(regionId = 10, text = "Která památka UNESCO je v Třebíči?", options = listOf("Bazilika", "Zámek", "Kostel", "Věž"), correctAnswerIndex = 0))
            q.add(Question(regionId = 10, text = "Které hory jsou v tomto kraji?", options = listOf("Žďárské vrchy", "Jihlavské vrchy", "Českomoravská vrchovina", "Vše"), correctAnswerIndex = 3))

            // --- 11. JIHOMORAVSKÝ ---
            q.add(Question(regionId = 11, text = "Která propast tu leží?", options = listOf("Macocha", "Hranická", "Punkevní", "Sloupská"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Který hrad je dominantou Brna?", options = listOf("Špilberk", "Veveří", "Pernštejn", "Bítov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Co je typickým produktem kraje?", options = listOf("Víno", "Pivo", "Olej", "Uhlí"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Jak se jmenuje NP u Znojma?", options = listOf("Podyjí", "Šumava", "Krkonoše", "Pálava"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Která vila v Brně je v UNESCO?", options = listOf("Tugendhat", "Stiassni", "Jurkovičova", "Low-Beer"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Jak se jmenuje pohoří u Mikulova?", options = listOf("Pálava", "Chřiby", "Bílé Karpaty", "Beskydy"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Která řeka protéká Brnem?", options = listOf("Svratka", "Svitava", "Dyje", "Obě první"), correctAnswerIndex = 3))
            q.add(Question(regionId = 11, text = "Kde se konala bitva tří císařů?", options = listOf("Slavkov", "Vyškov", "Bučovice", "Rousínov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Který areál je v UNESCO?", options = listOf("Lednicko-valtický", "Kroměřížský", "Třeboňský", "Pražský"), correctAnswerIndex = 0))
            q.add(Question(regionId = 11, text = "Jak se jmenuje vodní nádrž?", options = listOf("Nové Mlýny", "Brněnská", "Vranov", "Dalešice"), correctAnswerIndex = 0))

            // --- 12. OLOMOUCKÝ ---
            q.add(Question(regionId = 12, text = "Specialita z Loštic?", options = listOf("Tvarůžky", "Perník", "Uši", "Trubičky"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Sloup v Olomouci (UNESCO)?", options = listOf("Nejsvětější Trojice", "Mariánský", "Morový", "Čestný"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Které hory jsou na severu?", options = listOf("Jeseníky", "Beskydy", "Orlické hory", "Krkonoše"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Nejvyšší hora Jeseníků?", options = listOf("Praděd", "Keprník", "Šerák", "Dlouhé stráně"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Která řeka protéká krajem?", options = listOf("Morava", "Bečva", "Odra", "Desná"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Kde je nejhlubší propast světa?", options = listOf("Hranice", "Macocha", "Punkevní", "Zbrašov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Jak se jmenuje hrad u Přerova?", options = listOf("Helfštýn", "Bouzov", "Šternberk", "Mírov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Který hrad je známý z pohádek?", options = listOf("Bouzov", "Šovinec", "Úsov", "Potštejn"), correctAnswerIndex = 0))
            q.add(Question(regionId = 12, text = "Které lázně jsou v Jeseníkách?", options = listOf("Priessnitz", "Karlova Studánka", "Velké Losiny", "Vše"), correctAnswerIndex = 3))
            q.add(Question(regionId = 12, text = "Kde se vyrábí papír ručně?", options = listOf("Velké Losiny", "Šumperk", "Zábřeh", "Mohelnice"), correctAnswerIndex = 0))

            // --- 13. ZLÍNSKÝ ---
            q.add(Question(regionId = 13, text = "Kdo proslavil Zlín?", options = listOf("Baťa", "Škoda", "Křižík", "Kolben"), correctAnswerIndex = 0))
            q.add(Question(regionId = 13, text = "Zahrady UNESCO jsou v?", options = listOf("Kroměříž", "Uherské Hradiště", "Zlín", "Vsetín"), correctAnswerIndex = 0))
            q.add(Question(regionId = 13, text = "Největší lázně Moravy?", options = listOf("Luhačovice", "Kostelec", "Buchlovice", "Smraďavka"), correctAnswerIndex = 0))
            q.add(Question(regionId = 13, text = "Které hory jsou na východě?", options = listOf("Beskydy", "Bílé Karpaty", "Javorníky", "Všechny"), correctAnswerIndex = 3))
            q.add(Question(regionId = 13, text = "Který hrad se tyčí nad krajem?", options = listOf("Buchlov", "Malenovice", "Brumov", "Lukov"), correctAnswerIndex = 0))
            q.add(Question(regionId = 13, text = "Které poutní místo je nejznámější?", options = listOf("Hostýn", "Velehrad", "Svatý Kopeček", "Provodov"), correctAnswerIndex = 1))
            q.add(Question(regionId = 13, text = "Čím jsou známé Vizovice?", options = listOf("Slivovicí", "Pečivem", "Sýrem", "Pivem"), correctAnswerIndex = 0))
            q.add(Question(regionId = 13, text = "Jak se jmenuje řeka ve Zlíně?", options = listOf("Dřevnice", "Morava", "Bečva", "Olšava"), correctAnswerIndex = 0))
            q.add(Question(regionId = 13, text = "Kde najdeme skanzen?", options = listOf("Rožnov", "Modrá", "Rymice", "Všechny"), correctAnswerIndex = 3))
            q.add(Question(regionId = 13, text = "Jak se jmenuje hora se sochou Radegasta?", options = listOf("Radhošť", "Pustevny", "Lysá hora", "Smrk"), correctAnswerIndex = 0))

            // --- 14. MORAVSKOSLEZSKÝ ---
            q.add(Question(regionId = 14, text = "Krajské město?", options = listOf("Ostrava", "Opava", "Havířov", "Karviná"), correctAnswerIndex = 0))
            q.add(Question(regionId = 14, text = "Co se tu těžilo?", options = listOf("Černé uhlí", "Hnědé uhlí", "Ropa", "Zinek"), correctAnswerIndex = 0))
            q.add(Question(regionId = 14, text = "Nejvyšší hora Beskyd?", options = listOf("Lysá hora", "Smrk", "Kněhyně", "Radhošť"), correctAnswerIndex = 0))
            q.add(Question(regionId = 14, text = "Technická památka v Ostravě?", options = listOf("Vítkovice", "Landek", "Michal", "Všechny"), correctAnswerIndex = 3))
            q.add(Question(regionId = 14, text = "Která řeka protéká Ostravou?", options = listOf("Odra", "Ostravice", "Opava", "Všechny tři"), correctAnswerIndex = 3))
            q.add(Question(regionId = 14, text = "Jak se jmenuje hrad v kraji?", options = listOf("Hukvaldy", "Sovinec", "Štramberk", "Všechny"), correctAnswerIndex = 3))
            q.add(Question(regionId = 14, text = "Co jsou Štramberské uši?", options = listOf("Pečivo", "Skály", "Jeskyne", "Náušnice"), correctAnswerIndex = 0))
            q.add(Question(regionId = 14, text = "Které město je známé výrobou klobouků?", options = listOf("Nový Jičín", "Příbor", "Kopřivnice", "Frýdek"), correctAnswerIndex = 0))
            q.add(Question(regionId = 14, text = "Kde se vyrábí automobily Hyundai?", options = listOf("Nošovice", "Třinec", "Bohumín", "Bruntál"), correctAnswerIndex = 0))
            q.add(Question(regionId = 14, text = "Které město je známé Tatrou?", options = listOf("Kopřivnice", "Studénka", "Vítkov", "Krnov"), correctAnswerIndex = 0))

            questionDao.insertQuestions(q)
        }
    }
}
