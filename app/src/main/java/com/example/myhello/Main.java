//package fr.ec.android.todo;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import fr.ec.android.todo.model.ItemToDo;
//import fr.ec.android.todo.model.ListeToDo;
//import fr.ec.android.todo.model.ProfilListeToDo;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ProfilListeToDo p;
//    private EditText et;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        et = (EditText)findViewById(R.id.editTextMessage);
//
//
//        /* Test de création de fichier   */
//        /* ============================= */
//
//        p= new ProfilListeToDo("pierre");
//        ListeToDo l = new ListeToDo("Marché");
//        ItemToDo item1 = new ItemToDo("légumes");
//        ItemToDo item2 = new ItemToDo("viande");
//        ItemToDo item3 = new ItemToDo("oeufs");
//
//        l.ajouterItem(item1);
//        l.ajouterItem(item2);
//        l.ajouterItem(item3);
//
//        p.ajouteListe(l);
//
//        /* Sérialisation en JSON */
//         GsonBuilder builder = new GsonBuilder();
//         Gson gson = builder.create();
//        String sJson = gson.toJson(p);
//
//        /* Sauvegarde de la chaine sJson dans un fichier local */
//        String filename = "unFichierJson";
//
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(sJson.getBytes());
//            outputStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        /* ======================================================================== */
//        /* Version avec init du profil depuis  fichier JSON dans la mémoire interne */
//        /* ======================================================================== */
//        testFromMemoireInterne();
//        Log.i("TODO_ISA","Profil stocké dans un fichier JSON  : " + p.toString());
//
//        /* ========================================================================== */
//        /* Version avec init du profil depuis les préférences                         */
//        /* ========================================================================== */
//        testFromSharedPreferences();
//
//        Log.i("TODO_ISA","Profil stocké dans les préférences  : " + p.toString());
//    }
//    /* Traitement de la toolbar par défaut et du menu associé */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        Toast myToast = Toast.makeText(this,item.getTitle(),Toast.LENGTH_LONG);
//        myToast.show();
//        return super.onOptionsItemSelected(item);
//    }
//    /* Lancement de la deuxième activité */
//    public void ouvrirSecondeActivity(View v)
//    {
//        /* Creation d'un bundle pour passer des données à la seonde activité */
//        Bundle myBdl = new Bundle();
//        /* Extraction du texte présent dans le champ d'édition */
//        String s = et.getText().toString();
//        /* Initialisation de la clef "message" du Bundle avec la valeur extraite du champs d'édition */
//        myBdl.putString("message",s);
//
//        /* Création de l'intent de lancement de la seconde activité */
//        Intent myIntent;
//        myIntent = new Intent(MainActivity.this,secondeActivity.class);
//
//        /* Ajout du Bundle créé dans l'intent d'ouverturre de la nouvelle activité */
//        myIntent.putExtras(myBdl);
//
//        /* Lancement de la nouvelle activité */
//        startActivity(myIntent);
//
//    }
//
//
//    public void testFromSharedPreferences()
//    {
//        /* Préférences de l'activité */
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        /* Préférences de l'application */
//        SharedPreferences preferencesAppli = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editorAppli = preferencesAppli.edit();
//        editorAppli.putString("cle","valeur");
//        editorAppli.apply();
//        editorAppli.commit();
//
//        final GsonBuilder builder = new GsonBuilder();
//        final Gson gson = builder.create();
//
//        String s="aucune";
//        /* Recherche du profil stocké actuellement dans les préférences */
//        s = preferences.getString("profil","aucune");
//        if (s.equals("aucune")) {
//            /* Creation d'un profil par défaut */
//            this.p = profilParDefaut();
//
//           /* s = gson.toJson(ps);
//            editor.putString("profil", s);
//            editor.apply();*/
//
//            /* Evolution des données */
//            p.setLogin("Marie");
//
//            /* Création d'une nouvelle toDoDoListe */
//            ListeToDo cuisine = new ListeToDo("Cuisine");
//            cuisine.ajouterItem(new ItemToDo("Carbonnades"));
//            cuisine.ajouterItem(new ItemToDo("Brioche"));
//            cuisine.ajouterItem(new ItemToDo("Riz au lait"));
//            cuisine.ajouterItem(new ItemToDo("Salade piemontaise"));
//
//            p.ajouteListe(cuisine);
//
//            Log.i("TODO_ISA", "Creation d'une nouvelle liste cuisine : " + cuisine.toString());
//
//            /* Sauvegarde dans les préférences */
//
//
//            s = gson.toJson(p);
//
//
//            editor.putString("profil", s);
//            editor.apply();
//        }
//        else
//        {
//            this.p = (ProfilListeToDo)gson.fromJson(s,ProfilListeToDo.class);
//            Log.i("TODO_ISA","Préférences au format JSON : " + s);
//        }
//    }
//    public void testFromMemoireInterne()
//    {
//        /* Recuperation du fichier de sauvegarde MONPROFIL.JSON */
//        p = importProfil();
//        TextView tv = (TextView) findViewById(R.id.tvMessage);
//        tv.setText("Bonjour " + p.getLogin());
//
//        /* Evolution des données */
//        //p.setLogin("Albert");
//
//        /* Création d'une nouvelle toDoDoListe */
//        ListeToDo loisirs = new ListeToDo("Loisirs");
//        loisirs.ajouterItem(new ItemToDo("Cinéma"));
//        loisirs.ajouterItem(new ItemToDo("Théatre"));
//        loisirs.ajouterItem(new ItemToDo("Footing"));
//        loisirs.ajouterItem(new ItemToDo("concert"));
//
//
//        p.ajouteListe(loisirs);
//
//        Log.i("TODO_ISA","Creation d'une nouvelle liste loisirs : "+ loisirs.toString());
//        /* Sauvegarde de mon profil en JSON */
//        ProfilToJsonFile();
//    }
//    public  ProfilListeToDo importProfil()
//    {
//        final GsonBuilder builder = new GsonBuilder();
//        final Gson gson = builder.create();
//        String filename = "monprofil_json";
//
//        FileInputStream inputStream;
//        String sJsonLu="";
//
//        ProfilListeToDo profil = new ProfilListeToDo();
//
//
//        /* Import du fichier JSON de sauvegarde dans l'objet */
//
//        try {
//            inputStream = openFileInput(filename);
//            int content;
//            while ((content = inputStream.read()) != -1) {
//                // convert to char and display it
//                sJsonLu = sJsonLu+(char)content;
//            }
//            inputStream.close();
//
//            profil = (ProfilListeToDo)gson.fromJson(sJsonLu,ProfilListeToDo.class);
//        }
//        catch (Exception e) {
//
//            /* Creation d'un profil par defaut */
//            profil = profilParDefaut();
//            Log.i("TODO_ISA","Création du profil par défaut " + profil.getLogin());
//
//            String fileContents = gson.toJson(profil);
//            FileOutputStream outputStream;
//
//            try {
//                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//                outputStream.write(fileContents.getBytes());
//                outputStream.close();
//                Log.i("TODO_ISA","Création du fichier monprofil_json");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                Log.i("TODO_ISA","Impossible de créer le fichier de sauvegarde du profil par défaut");
//            }
//
//
//        }
//
//        return profil;
//    }
//    public void ProfilToJsonFile()
//    {
//        final GsonBuilder builder = new GsonBuilder();
//        final Gson gson = builder.create();
//        String filename = "monprofil_json";
//        String fileContents = gson.toJson(this.p);
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(fileContents.getBytes());
//            outputStream.close();
//            Log.i("TODO_ISA","Sauvegarde du fichier monprofil_json");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public static ProfilListeToDo profilParDefaut()
//    {
//        ProfilListeToDo pDefaut = new ProfilListeToDo("Isabelle");
//        return pDefaut;
//    }
//
//
//}
