<h1>Big files finder</h1>
<b>Zadaním úlohy bolo nájs N najväèších súborov vo zvolenıch adresároch.</b>

Aplikácia sa skladá z 2 architekturálnych èastí</br></br>

<h2>MainActivity</h2>
	Predstavuje „klientskú“ èas, obsahuje GUI ktoré ovláda vstupy pre servisu.
	Skladá sa z 4 fragmentov
	<li><b>WelcomeFragment<b>
		Predstavuje len úvodnú obrazovku, ukáka komplexnejších animaènıch techník a material designu</li>

	b.	FolderPickerFragment
		UI pre vıber adresárov, je moné prechádza aktuálnu súborovú štruktúru a prídáva si adresáre, ktoré budú preh¾adávané. Zvolené adresáre su zobrazené v FileChipsView, èo je subclassa FlowLayout-u, ktorá je obdobou LinearLayoutu taká, e ak sa View zmestí do riadku tak ho dá do toho istého riadku, ak nie tak na novı riadok. 
		Ako list je pouitı RecyclerView, ktorı sa lepšie chová s Transition animáciami ako ListView. V pravej èasti riadku je taktie znázornenı stav súboru (zašktrnutie / plus)

	c.	CountPickerFragment
		Zvolenie poètu najväèších h¾adanıch súborov

	d.	ResultsFragment
		Reaguje na broadcasty FinderServicu, a zobrazuje progressbar/vısledky/chybu vıpoètu.
		Kde kadı z nich predstavuje logickı krok zadávania vstupov pre vıpoèet.

<h2>FinderService</h2>
	<p>Predstavuje „serverovú“ èas, ktorá vykonáva samotné preh¾adávanie. Service bol zvolenı preto lebo aktivita je „len GUI“, ktoré po odchode do pozadia,môe kedyko¾vek zaniknú a preto nie je vhodnım rodièom pre potenciálne dlhotrvajúce thready vıpoètu. Tzn, vıpoèe je nezávislı od GUI, a to je dobre. Komunikácia medzi nimi prebieha pomocou intentov (broadcastov)</p>
	<p>Vstupom pre service je poèet ko¾ko najvaèších súborov h¾adáme a cesty na adresáre, ktoré majú by preh¾adávané.</p>
	<p>Po oèístení o neplatné vstupy, service optimalizuje zadané cesty adresárov vyhodením duplikátov a ciest ktoré su podadresárom niektorıch z ostatnıch ciest a nemá zmysel ich preh¾adáva (v princípe, algoritmus kontroluje prefixy ciest)</p>
	<p>Potom pre kadı prekonvertovanı File, vytvára FindLargestFilesTask asynchrónny task, ktorı beí na vlastnom Thread-e. Tasky sú spúšané na exekútore, tzn. všetky thready beia paralelne per-file. V tele tasku sa preh¾adáva cesta rekurízvne a ak súbor nie je adresár je pridanı do kolekcie.</p>
	<p>Zvolená kolekcia FilesBoundedPriorityQueue, ktorá predstavuje subclass-u PriorityQueue, èo je štandardná implementácia heap-u, ktorá nám zaruèí polo-zoradenie súborov s garantovanım najmenším súborom na vrchole. Pri pridávaní je avšak zvolená optimalizácia, kede vieme ko¾ko max. súborov h¾adáme, teda nemá zmysel, aby v heap-e boli všetky súbory ale len najvaèších N v danom momente. Teda pri pridávaní sa kontroluje, èí je vstup väèší ako súbor na vrchole (najmenší z heap-u), takı súbor je do heap-u pridanı a vrchol odstránenı. Takto èasom dostaneme N najvaèších súborov. (ešte podotknú, e heap-a je zoradená vzostupne, tzn najmenší je navrchole, teda je po vybratí prvkov z nej do listu, je potrebné list otoèi). Taktie metóda add() je synchronizovaná a garantuje thread-safety, keïe heap je zdie¾anı medzi thread-mi.</p>
	<p>Po dokonèení úlohy, je referencia naò odobratá zo Set-u beiacich úloh. Ak je set prázdny, boli vykonané všetky úlohy, teda vısledky vraciame cez broadcast naspa do UI a service ukonèujeme. (Samozrejme o zaèatí a skonèení vıpoètov notifikuje systémová notifikácia s progress-barom, resp. jednorázová o ukonèení všetkıch vıpoètov.). Pre prenos vısledkov bol pouítı ParcelableFile, èo je len POJO cesty, názvu a ve¾kosti súboru, keïe systémovı File neimplementuje Parcelable interface a nie je moné preposla ArrayList File-ov cez intent.</p>


