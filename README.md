# Mini Framework Servlet - Guide démarrage

## Installation rapide
1. Cloner le dépôt : `git clone https://github.com/Ramarosandratan/Sprint.git`
2. Basculer sur la branche : `git checkout sprint1-1015`
3. Déployer sur un serveur Tomcat ou équivalent

## Configuration web.xml
```xml
<servlet>
  <servlet-name>FrontController</servlet-name>
  <servlet-class>com.sprint.controller.FrontController</servlet-class>
  <init-param>
    <param-name>controller-package</param-name>
    <param-value>com.sprint.controller</param-value>
  </init-param>
</servlet>

<servlet-mapping>
  <servlet-name>FrontController</servlet-name>
  <url-pattern>/</url-pattern>
</servlet-mapping>
```

## Rôles

### Annotation @AnnotationController
- Marque les classes contrôleurs
- Doit être placée au-dessus de chaque classe contrôleur
- Exemple :
  ```java
  @AnnotationController
  public class MonControleur extends HttpServlet {...}
  ```

### FrontController
- Servlet principale qui gère toutes les requêtes
- Scan le package spécifié pour trouver les contrôleurs annotés
- Affiche la liste des contrôleurs détectés

### Ajout de nouveaux contrôleurs
1. Créer une nouvelle classe dans le package `com.sprint.controller`
2. Annoter avec `@AnnotationController`
3. Étendre `HttpServlet`
4. Implémenter les méthodes `doGet`/`doPost`

## Limitations
- Le scan des classes se fait uniquement au démarrage de l'application
- Ne détecte pas les contrôleurs dans les sous-packages
- Nécessite un rechargement de l'application pour ajouter de nouveaux contrôleurs

## Commandes Git pour le sprint
```bash
git checkout -b sprint1-1015
# Après modifications
git add .
git commit -m "Implémentation mini framework avec scan annotations"
git push origin sprint1-1015
