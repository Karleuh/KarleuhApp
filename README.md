# KarleuhApp
 
DONE :
- Tout le TP1 (avec binding et view holder)
- TP2 jusqu'à l'étape 7 (il manque donc l'interface tasklistlistener, le partage, la vue paysage)
- Tout le TP3 : intéraction avec todoist en direct dans le sens app -> todoist, mais a besoin d'une interaction pour trigger le refresh
dans l'autre sens (j'appelle simplement le refresh dans onResume)
- TP4 jusqu'à l'étape 9 mais sans l'étape 6 : tout sauf les bonus et les permissions
PickPhoto, command de l'api SYNC pour modifier les propriétés, tout semble marcher
- TP5 le Login (étape 3) vous devriez pouvoir vous connecter à votre compte todoist (en tout cas avec google ça marche)

TODO : 
- fin du TP2 
- permissions anciennes versions android
- revoir les coroutines et les appels api, il y a des petit problèmes de performance à vu de nez et d'après logcat alors qu'on ne fait pas grand chose
- faire en sorte que la photo de profil fetch soit aussi dans l'activité pour la modifier

PROBLEME CONNU :
- la 1ere intéraction est parfois sans effet tout n'a pas le temps de se configurer

REMARQUES :
- J'ai des doutes sur mon implémentation du load d'image par URI parce que c'est de basse qualité dans mon taskListFragment (dans la user activity ça va mais on y récupère directement l'image c'est de la triche) 
donc ce qui passe par le réseau doit être mal compressé (même après avoir complètement enlevé les bitmaps du code)
- Les dépendances pour le viewModel sont complexes il y a de bonnes chances de tomber sur des incompatibilités et les infos sur quelles versions 
utiliser ne sont pas immédiatement trouvables sur internet (trouvables quand même)
- Des imports android ont fait l'affaire plutôt que de coil pour pas mal de choses
- L'IDE pleure pour beaucoup de choses à tort (je crois) et à raison (c'est sûr)