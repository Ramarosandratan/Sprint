# Guide de Déploiement - Sprint Framework

## 🏗️ Construction de l'Application

### Construction complète (multi-modules)
```bash
mvn clean install
```

### Construction du module Demo uniquement
```bash
mvn -pl demo clean package
```

Le fichier WAR sera généré dans : `demo/target/sprint-demo.war`

## 🚀 Déploiement sur Tomcat

### Méthode 1 : Déploiement manuel
```bash
# Copier le WAR dans le dossier webapps de Tomcat
copy demo\target\sprint-demo.war C:\path\to\tomcat\webapps\sprint-demo.war

# Démarrer Tomcat
C:\path\to\tomcat\bin\startup.bat
```

### Méthode 2 : Déploiement automatique (Tomcat Manager)
1. Accéder à : http://localhost:8080/manager/html
2. Section "Deploy" → "WAR file to deploy"
3. Sélectionner le fichier `sprint-demo.war`

## 🧪 Tests de Validation Post-Déploiement

### 1. Tests de Base
| Test | URL | Résultat Attendu |
|------|-----|------------------|
| **Page d'accueil** | http://localhost:8080/sprint-demo/ | Page d'accueil avec liens |
| **Hello Demo** | http://localhost:8080/sprint-demo/demo/hello | Message "Hello from demo module!" |
| **Info Framework** | http://localhost:8080/sprint-demo/demo/info | Informations sur le framework |
| **Test User** | http://localhost:8080/sprint-demo/demo/user | Formulaire utilisateur |

### 2. Tests avec curl
```bash
# Test Hello
curl http://localhost:8080/sprint-demo/demo/hello

# Test Info
curl http://localhost:8080/sprint-demo/demo/info

# Test avec paramètres
curl "http://localhost:8080/sprint-demo/demo/user?name=John&email=john@example.com"
```

### 3. Tests de Formulaires
```bash
# Test formulaire utilisateur
curl -X POST http://localhost:8080/sprint-demo/demo/user \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Jane&email=jane@example.com&age=25"
```

### 4. Tests d'Erreur
| Test | URL | Résultat Attendu |
|------|-----|------------------|
| **404 Not Found** | http://localhost:8080/sprint-demo/demo/nonexistent | Page d'erreur 404 |
| **Méthode non autorisée** | POST http://localhost:8080/sprint-demo/demo/hello | Erreur 405 |

## 🔧 Configuration Tomcat

### server.xml (Configuration de base)
```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

### web.xml (Configuration de l'application)
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

## 🐛 Dépannage

### Problèmes courants et solutions

#### 1. Erreur 404 - Page non trouvée
- **Cause**: Mauvais nom de contexte ou URL incorrecte
- **Solution**: Vérifier le nom du WAR déployé et l'URL utilisée

#### 2. Erreur 500 - Erreur interne
- **Cause**: Problème de configuration ou exception non gérée
- **Solution**: 
  - Vérifier les logs Tomcat: `C:\path\to\tomcat\logs\catalina.out`
  - Vérifier la configuration du package des contrôleurs

#### 3. Erreur de démarrage Tomcat
- **Cause**: Port 8080 déjà utilisé
- **Solution**: 
  - Modifier le port dans `server.xml`
  - Ou arrêter le processus utilisant le port 8080

#### 4. Erreur de compilation
- **Cause**: Dépendances manquantes
- **Solution**: 
  ```bash
  mvn clean install -U
  ```

### Logs à vérifier
1. **Logs Tomcat**: `C:\path\to\tomcat\logs\catalina.out`
2. **Logs de l'application**: `C:\path\to\tomcat\logs\localhost.log`
3. **Logs d'accès**: `C:\path\to\tomcat\logs\localhost_access_log.txt`

## 📊 Monitoring

### Vérification du déploiement
```bash
# Vérifier que l'application est déployée
curl -I http://localhost:8080/sprint-demo/

# Vérifier les endpoints
curl -I http://localhost:8080/sprint-demo/demo/hello
```

### Vérification des logs en temps réel
```bash
# Sur Windows
tail -f C:\path\to\tomcat\logs\catalina.out

# Sur Linux/Mac
tail -f /path/to/tomcat/logs/catalina.out
```

## 🔄 Redéploiement

### Redéploiement rapide
```bash
# Arrêter Tomcat
C:\path\to\tomcat\bin\shutdown.bat

# Supprimer l'ancien WAR et le dossier déployé
del C:\path\to\tomcat\webapps\sprint-demo.war
rmdir /s C:\path\to\tomcat\webapps\sprint-demo

# Redéployer
copy demo\target\sprint-demo.war C:\path\to\tomcat\webapps\

# Redémarrer Tomcat
C:\path\to\tomcat\bin\startup.bat
```

## 📋 Checklist de Validation

- [ ] Tomcat démarre sans erreur
- [ ] Le WAR est correctement déployé
- [ ] La page d'accueil est accessible
- [ ] Les endpoints de démo fonctionnent
- [ ] Les formulaires fonctionnent
- [ ] La gestion d'erreur est opérationnelle
- [ ] Les logs ne montrent pas d'erreurs critiques
