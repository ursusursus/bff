<h1>Big files finder</h1>
<b>Zadan�m �lohy bolo n�js� N najv���ch s�borov vo zvolen�ch adres�roch.</b>

Aplik�cia sa sklad� z 2 architektur�lnych �ast�</br></br>

<h2>MainActivity</h2>
	Predstavuje �klientsk�� �as�, obsahuje GUI ktor� ovl�da vstupy pre servisu.
	Sklad� sa z 4 fragmentov
	<li><b>WelcomeFragment<b>
		Predstavuje len �vodn� obrazovku, uk�ka komplexnej��ch anima�n�ch techn�k a material designu</li>

	b.	FolderPickerFragment
		UI pre v�ber adres�rov, je mo�n� prech�dza� aktu�lnu s�borov� �trukt�ru a pr�d�va� si adres�re, ktor� bud� preh�ad�van�. Zvolen� adres�re su zobrazen� v FileChipsView, �o je subclassa FlowLayout-u, ktor� je obdobou LinearLayoutu tak�, �e ak sa View zmest� do riadku tak ho d� do toho ist�ho riadku, ak nie tak na nov� riadok. 
		Ako list je pou�it� RecyclerView, ktor� sa lep�ie chov� s Transition anim�ciami ako ListView. V pravej �asti riadku je taktie� zn�zornen� stav s�boru (za�ktrnutie / plus)

	c.	CountPickerFragment
		Zvolenie po�tu najv���ch h�adan�ch s�borov

	d.	ResultsFragment
		Reaguje na broadcasty FinderServicu, a zobrazuje progressbar/v�sledky/chybu v�po�tu.
		Kde ka�d� z nich predstavuje logick� krok zad�vania vstupov pre v�po�et.

<h2>FinderService</h2>
	<p>Predstavuje �serverov�� �as�, ktor� vykon�va samotn� preh�ad�vanie. Service bol zvolen� preto lebo aktivita je �len GUI�, ktor� po odchode do pozadia,m��e kedyko�vek zanikn�� a preto nie je vhodn�m rodi�om pre potenci�lne dlhotrvaj�ce thready v�po�tu. Tzn, v�po�e� je nez�visl� od GUI, a to je dobre. Komunik�cia medzi nimi prebieha pomocou intentov (broadcastov)</p>
	<p>Vstupom pre service je po�et ko�ko najva��ch s�borov h�ad�me a cesty na adres�re, ktor� maj� by� preh�ad�van�.</p>
	<p>Po o��sten� o neplatn� vstupy, service optimalizuje zadan� cesty adres�rov vyhoden�m duplik�tov a ciest ktor� su podadres�rom niektor�ch z ostatn�ch ciest a nem� zmysel ich preh�ad�va� (v princ�pe, algoritmus kontroluje prefixy ciest)</p>
	<p>Potom pre ka�d� prekonvertovan� File, vytv�ra FindLargestFilesTask asynchr�nny task, ktor� be�� na vlastnom Thread-e. Tasky s� sp���an� na exek�tore, tzn. v�etky thready be�ia paralelne per-file. V tele tasku sa preh�ad�va cesta rekur�zvne a ak s�bor nie je adres�r je pridan� do kolekcie.</p>
	<p>Zvolen� kolekcia FilesBoundedPriorityQueue, ktor� predstavuje subclass-u PriorityQueue, �o je �tandardn� implement�cia heap-u, ktor� n�m zaru�� polo-zoradenie s�borov s garantovan�m najmen��m s�borom na vrchole. Pri prid�van� je av�ak zvolen� optimaliz�cia, ked�e vieme ko�ko max. s�borov h�ad�me, teda nem� zmysel, aby v heap-e boli v�etky s�bory ale len najva��ch N v danom momente. Teda pri prid�van� sa kontroluje, �� je vstup v��� ako s�bor na vrchole (najmen�� z heap-u), tak� s�bor je do heap-u pridan� a vrchol odstr�nen�. Takto �asom dostaneme N najva��ch s�borov. (e�te podotkn��, �e heap-a je zoraden� vzostupne, tzn najmen�� je navrchole, teda je po vybrat� prvkov z nej do listu, je potrebn� list oto�i�). Taktie� met�da add() je synchronizovan� a garantuje thread-safety, ke�e heap je zdie�an� medzi thread-mi.</p>
	<p>Po dokon�en� �lohy, je referencia na� odobrat� zo Set-u be�iacich �loh. Ak je set pr�zdny, boli vykonan� v�etky �lohy, teda v�sledky vraciame cez broadcast naspa� do UI a service ukon�ujeme. (Samozrejme o za�at� a skon�en� v�po�tov notifikuje syst�mov� notifik�cia s progress-barom, resp. jednor�zov� o ukon�en� v�etk�ch v�po�tov.). Pre prenos v�sledkov bol pou��t� ParcelableFile, �o je len POJO cesty, n�zvu a ve�kosti s�boru, ke�e syst�mov� File neimplementuje Parcelable interface a nie je mo�n� preposla� ArrayList File-ov cez intent.</p>


