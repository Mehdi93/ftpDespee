package com.ftp.clientftp;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ListView liste = null;
	private File rep = null;
	private String[] nomsFichiers = null;
	private List<String> nomsListe = null;
	private ArrayAdapter<String> adapter = null;
	private boolean quit = false;
	private String pathFichier = null;
	private String pathDossier = null;
	private String pathParent = null;
	private String saisie = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//on actualise la liste par rapport au repertoire courant
		creerListe(Environment.getExternalStorageDirectory().toString());
	    
	    //on param�tre les click courts
	    liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	  @Override
	    	  public void onItemClick(AdapterView<?> adapterView, View view, int position,long id) {
	    		  
	    		  String nomClick = liste.getItemAtPosition((int)id).toString();
    			  rep = new File(rep.getPath()+"/"+nomClick);
	    		  
	    		  if(rep.isDirectory()) //si c'est un repertoire,on l'ouvre
	    		  {
	    			  pathDossier=rep.getPath();
	    			  creerListe(rep.getPath());
	    			  quit=false;
	    			  
	    		  }
	    		  else //si c'est un fichier,on ouvre le menu contextuel
	    		  {
	    			  pathFichier=rep.getAbsolutePath(); //on r�cup�re le chemin du fichier dans le cas d'une op�ration
	    			  rep = new File(rep.getParent());
	    			  pathParent=rep.getAbsolutePath();
	    			  quit=false;
	    			  openContextMenu(view);
	    			  
	    		  }
	    	  }
	    	});
	    
	    registerForContextMenu(liste); //on associe la menu context � notre liste
	}
	
	public void boiteDialogRenommerFichier()
	{
		// R�cup�ration des ressources
		LayoutInflater inflater= LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.dialog1, null);

		// Cr�ation du builder
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Saisissez le nouveau nom");
		dialogBuilder.setView(textEntryView);

		// Ajout du gestionnaire d'�v�nements
		dialogBuilder.setPositiveButton("Ok",
		 new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			   	 EditText champ = (EditText)textEntryView.findViewById(R.id.EditText1);
			     File newNom = new File(rep.getPath()+"/"+champ.getText().toString());
			     rep = new File(pathFichier);
			     rep.renameTo(newNom);
			     rep = new File(rep.getParent());
			     creerListe(rep.getPath()); //on actualise la liste
		   }
		});
		dialogBuilder.setNegativeButton("Annuler", null);
		
		dialogBuilder.show();
	}
	
	public void boiteDialogNewDossier()
	{
		// R�cup�ration des ressources
		LayoutInflater inflater= LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.dialog1, null);

		// Cr�ation du builder
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Saisissez le nom du r�pertoire");
		dialogBuilder.setView(textEntryView);

		// Ajout du gestionnaire d'�v�nements
		dialogBuilder.setPositiveButton("Ok",
		 new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			    EditText champ = (EditText)textEntryView.findViewById(R.id.EditText1);
			     
		     	rep = new File(rep.getPath()+"/"+champ.getText().toString());
	        	rep.mkdir();
	        	rep = new File(rep.getParent());
		     	
			    creerListe(rep.getPath()); //on actualise la liste
		   }
		});
		dialogBuilder.setNegativeButton("Annuler", null);
		
		dialogBuilder.show();
	}
	
	public void boiteDialogNewFichier()
	{
		// R�cup�ration des ressources
		LayoutInflater inflater= LayoutInflater.from(this);
		final View textEntryView = inflater.inflate(R.layout.dialog1, null);

		// Cr�ation du builder
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Saisissez le nom du fichier � cr�er");
		dialogBuilder.setView(textEntryView);

		// Ajout du gestionnaire d'�v�nements
		dialogBuilder.setPositiveButton("Ok",
		 new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int which) {
			    EditText champ = (EditText)textEntryView.findViewById(R.id.EditText1);
			    
	        	rep = new File(rep.getPath()+"/"+champ.getText().toString());
	        	try {
					rep.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	rep = new File(rep.getParent());
		     	
			    creerListe(rep.getPath()); //on actualise la liste
		   }
		});
		dialogBuilder.setNegativeButton("Annuler", null);
		
		dialogBuilder.show();
	}
	
	public void creerListe(String chemin){
		//on ouvre le r�pertoire courant
		
		rep = new File(chemin);
		nomsFichiers = rep.list();
		
		liste = (ListView) findViewById(R.id.listView1); //on cr�er la liste qui sera affich�e
	    nomsListe = new ArrayList<String>();
		
		for(int i=0;i<nomsFichiers.length;i++) //on affiche tout les fichiers du r�pertoire courant
		{
			nomsListe.add(nomsFichiers[i]);
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomsListe);
	    liste.setAdapter(adapter);
	}   
	
	@Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    super.onCreateContextMenu(menu, v, menuInfo);   
        menu.add(0, v.getId(), 0, "Envoyer"); 
        menu.add(0, v.getId(), 0, "Renommer"); 
        menu.add(0, v.getId(), 0, "Supprimer");
        menu.add(0, v.getId(), 0, "Cr�er un fichier");
        menu.add(0, v.getId(), 0, "Cr�er un r�pertoire");
    }  
  
    @Override  
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Envoyer")){
        	
        	Toast.makeText(this,"Envoyer",Toast.LENGTH_SHORT).show(); 
        }  
        else if(item.getTitle().equals("Renommer")){
        	
        	Toast.makeText(this,"Renommer",Toast.LENGTH_SHORT).show();
        	
        	boiteDialogRenommerFichier();
        	
        }
        else if(item.getTitle().equals("Supprimer")){
        	
        	Toast.makeText(this,"Supprimer",Toast.LENGTH_SHORT).show();

        	//on supprime le fichier
        	rep = new File(pathFichier);
        	rep.delete();
        	
        	rep = new File(pathParent);
        	creerListe(rep.getPath()); //on actualise la liste
        }
        else if(item.getTitle().equals("Cr�er un fichier")){
        	
        	Toast.makeText(this,"Cr�er un fichier",Toast.LENGTH_SHORT).show();
        	
        	boiteDialogNewFichier();
        	
        	creerListe(rep.getPath());
        	
        }
        else if(item.getTitle().equals("Cr�er un r�pertoire")){
        	
        	Toast.makeText(this,"Cr�er un r�pertoire",Toast.LENGTH_SHORT).show();
        	
        	boiteDialogNewDossier();
        	creerListe(rep.getPath());
        } 
        else {
        	
        	return false;
        }  
    return true;  
    }  

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		//touche retour  
		if(keyCode == KeyEvent.KEYCODE_BACK) {
		    if(!rep.getPath().equals("/")) //on remonte si c'est possible
		    {
				rep = new File(rep.getParent());
			    creerListe(rep.getPath());
		    }
		    else
		    {
		    	if(quit==true) //si on appuie 2 fois sur retour,on ferme l'appli
		    	{
		    		finish();
		    	}
		    	else
		    	{
			    	quit=true;
			    	Toast.makeText(MainActivity.this,"Appuyer une 2�me fois pour quitter",Toast.LENGTH_SHORT).show();
		    	}
		    }
		    return true;
		 }
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
