# Sprint Framework Demo - Documentation

## 🎯 Vue d'ensemble

Ce module contient une application de démonstration complète qui illustre l'utilisation du Sprint Framework. Il comprend des contrôleurs d'exemple, des pages JSP, et des formulaires de test.

## 🚀 Endpoints de Test

### 1. Page d'accueil
- **URL**: http://localhost:8080/sprint-demo/
- **Description**: Page d'accueil avec navigation vers les différentes démos
- **Fichier JSP**: `index.jsp`

### 2. Hello Demo
- **URL**: http://localhost:8080/sprint-demo/demo/hello
- **Description**: Test basique du framework avec message dynamique
- **Contrôleur**: `DemoController.hello()`
- **Fichier JSP**: `hello.jsp`
- **Test**: Le timestamp doit changer à chaque rafraîchissement

### 3. Info Framework
- **URL**: http://localhost:8080/sprint-demo/demo/info
- **Description**: Affiche des informations sur le framework
- **Contrôleur**: `DemoController.info()`
- **Fichier JSP**: `info.jsp`

### 4. Test Utilisateur
- **URL**: http://localhost:8080/sprint-demo/demo/user
- **Description**: Formulaire de test avec gestion des paramètres
- **Méthodes**: GET (formulaire) et POST (soumission)
- **Contrôleur**: `DemoController.user()`
- **Fichiers JSP**: `user.jsp`, `user-data.jsp`

## 🧪 Tests avec curl

### Test Hello
```bash
curl http://localhost:8080/sprint-demo/demo/hello
```

### Test Info
```bash
curl http://localhost:8080/sprint-demo/demo/info
```

### Test avec paramètres GET
```bash
curl "http://localhost:8080/sprint-demo/demo/user?name=John&email=john@example.com&age=30"
```

### Test POST avec formulaire
```bash
curl -X POST http://localhost:8080/sprint-demo/demo/user \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Jane&email=jane@example.com&age=25"
```

## 📁 Structure du Module

```
demo/
├── src/main/java/
│   └── com/sprint/demo/
│       └── DemoController.java      # Contrôleur principal de démo
├── src/main/webapp/
│   ├── index.jsp                    # Page d'accueil
│   ├── hello.jsp                    # Page Hello Demo
│   ├── info.jsp                     # Page Info Framework
│   ├── user.jsp                     # Formulaire utilisateur
│   ├── user-data.jsp                # Affichage données utilisateur
│   ├── login.jsp                    # Page de connexion
│   ├── confirmation.jsp             # Page de confirmation
│   ├── test-form.html               # Formulaire HTML de test
│   └── test-user-form.html          # Formulaire utilisateur HTML
└── target/sprint-demo.war          # Application déployable
```

## 🎯 Exemples de Code

### Contrôleur DemoController
```java
package com.sprint.demo;

import com.sprint.annotation.*;
import com.sprint.framework.ModelView;
import com.sprint.model.User;

@AnnotationController
@url("/demo")
public class DemoController {
    
    @GET
    @url("/hello")
    public ModelView hello() {
        ModelView mv = new ModelView("hello.jsp");
        mv.addObject("message", "Hello from demo module!");
        mv.addObject("timestamp", System.currentTimeMillis());
        return mv;
    }
    
    @GET
    @url("/info")
    public ModelView info() {
        ModelView mv = new ModelView("info.jsp");
        mv.addObject("frameworkName", "Sprint Framework");
        mv.addObject("version", "1.0-SNAPSHOT");
        return mv;
    }
    
    @GET
    @url("/user")
    public ModelView userForm() {
        return new ModelView("user.jsp");
    }
    
    @POST
    @url("/user")
    public ModelView userSubmit(@Param("name") String name,
                               @Param("email") String email,
                               @Param("age") int age) {
        User user = new User();
        user.setFirstName(name);
        user.setEmail(email);
        user.setAge(age);
        
        ModelView mv = new ModelView("user-data.jsp");
        mv.addObject("user", user);
        return mv;
    }
}
```

## 🧪 Tests de Validation

### Tests à effectuer après déploiement :

1. **Test Page d'accueil**
   - Accès: http://localhost:8080/sprint-demo/
   - Vérifier: Affichage correct avec liens de navigation

2. **Test Hello**
   - Accès: http://localhost:8080/sprint-demo/demo/hello
   - Vérifier: Message "Hello from demo module!" et timestamp dynamique

3. **Test Info**
   - Accès: http://localhost:8080/sprint-demo/demo/info
   - Vérifier: Informations sur le framework affichées

4. **Test Formulaire**
   - Accès: http://localhost:8080/sprint-demo/demo/user
   - Vérifier: 
     - Formulaire s'affiche correctement
     - Soumission fonctionne avec GET et POST
     - Données sont correctement récupérées

5. **Test Erreur 404**
   - Accès: http://localhost:8080/sprint-demo/demo/nonexistent
   - Vérifier: Gestion d'erreur appropriée

## 🔧 Configuration

### web.xml (Configuration du FrontController)
```xml
<servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>com.sprint.controller.FrontController</servlet-class>
    <init-param>
        <param-name>controller-package</param-name>
        <param-value>com.sprint.demo</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

## 🚀 Déploiement Rapide

### Construction et déploiement
```bash
# Construire le WAR
mvn -pl demo clean package

# Déployer sur Tomcat
copy demo\target\sprint-demo.war C:\path\to\tomcat\webapps\
```

### Vérification du déploiement
```bash
# Vérifier que l'application est accessible
curl -I http://localhost:8080/sprint-demo/
```

## 📊 Monitoring

### Logs de l'application
Les logs peuvent être consultés dans :
- Tomcat: `C:\path\to\tomcat\logs\catalina.out`
- Application: `C:\path\to\tomcat\logs\localhost.log`

### Points de vérification
- [ ] Application démarre sans erreur
- [ ] Tous les endpoints sont accessibles
- [ ] Les formulaires fonctionnent correctement
- [ ] La gestion d'erreur est opérationnelle
- [ ] Les logs ne montrent pas d'erreurs critiques
