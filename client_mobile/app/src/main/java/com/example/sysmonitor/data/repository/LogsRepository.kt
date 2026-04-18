package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.LogAction
import com.example.sysmonitor.data.model.LogModel
import kotlinx.coroutines.delay

interface LogsRepository {
    suspend fun getLogs(): Result<List<LogModel>>
}

// ✅ MOCK — replace body with api.getLogs() when backend is ready
class MockLogsRepository : LogsRepository {

    override suspend fun getLogs(): Result<List<LogModel>> {
        delay(800)
        return Result.success(mockLogs)
    }

    private val mockLogs = listOf(
        LogModel("1",  "Saad",    LogAction.LOGIN,  "Connexion réussie depuis 192.168.1.10",  "08:02"),
        LogModel("2",  "Marwa",   LogAction.CREATE, "Création du projet 'App Mobile'",        "08:15"),
        LogModel("3",  "Admin",   LogAction.DELETE, "Suppression du serveur 'Backup-01'",     "08:30"),
        LogModel("4",  "Saad",    LogAction.UPDATE, "Mise à jour des permissions",            "08:45"),
        LogModel("5",  "Youssef", LogAction.LOGIN,  "Connexion depuis 10.0.0.5",              "09:00"),
        LogModel("6",  "Marwa",   LogAction.LOGOUT, "Déconnexion normale",                    "09:20"),
        LogModel("7",  "Admin",   LogAction.ERROR,  "Échec connexion base de données",        "09:35", true),
        LogModel("8",  "Saad",    LogAction.CREATE, "Nouveau serveur 'Web-Prod' ajouté",      "09:50"),
        LogModel("9",  "Fatima",  LogAction.LOGIN,  "Connexion depuis 172.16.0.3",            "10:05"),
        LogModel("10", "Admin",   LogAction.DELETE, "Suppression du projet 'Old-API'",        "10:20"),
        LogModel("11", "Youssef", LogAction.UPDATE, "Modification config serveur principal",  "10:35"),
        LogModel("12", "Marwa",   LogAction.CREATE, "Création alerte CPU > 90%",              "10:50"),
        LogModel("13", "Saad",    LogAction.LOGOUT, "Session expirée automatiquement",        "11:10"),
        LogModel("14", "Admin",   LogAction.ERROR,  "Timeout connexion serveur distant",      "11:25", true),
        LogModel("15", "Fatima",  LogAction.UPDATE, "Mise à jour mot de passe",               "11:40"),
        LogModel("16", "Youssef", LogAction.CREATE, "Déploiement version v2.3.1",             "11:55"),
        LogModel("17", "Saad",    LogAction.LOGIN,  "Reconnexion après expiration session",   "12:10"),
        LogModel("18", "Admin",   LogAction.DELETE, "Nettoyage logs anciens (>30j)",          "12:25"),
        LogModel("19", "Marwa",   LogAction.ERROR,  "Erreur permission /var/log/app",         "12:40", true),
        LogModel("20", "Fatima",  LogAction.LOGOUT, "Déconnexion manuelle",                   "12:55"),
    )
}