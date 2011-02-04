package com.scamall.app.mp3scanner;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.vaadin.Application;
import com.vaadin.ui.*;

public class MyVaadinApplication extends Application
                                   implements Button.ClickListener {
	private static final long serialVersionUID = 6894885505028149330L;
	
	Window mainWindow = new Window("Búsqueda de Mp3");
	VerticalLayout v = new VerticalLayout();
	HorizontalLayout ruta = new HorizontalLayout();
	HorizontalLayout busqueda = new HorizontalLayout();
	
	Table tabla = new Table();
	
	Label labelRuta = new Label("Ruta");
	TextField campoRuta = new TextField();
	CheckBox checkbox = new CheckBox("Recursivo");
	Button botonRuta = new Button("Actualizar");
	
	Label labelBusqueda = new Label("Búsqueda");
	TextField campoBusqueda = new TextField();
	NativeSelect criterioBusqueda = new NativeSelect();
	Button botonBusqueda = new Button("Buscar");
	
	Button botonTodos = new Button("Mostrar todos");
	Label info = new Label();

	SolrServer server;
	MP3Scanner scanner = new MP3Scanner();
	ArrayList<QuerySong> songs = new ArrayList<QuerySong>();
	ArrayList<QuerySong> querySongs = new ArrayList<QuerySong>();
	
	@Override
	public void init() {
		try {
			server = new CommonsHttpSolrServer("http://localhost:8082/solr");
		} catch (Exception e) {}
		
		botonBusqueda.addListener(this);
		botonRuta.addListener(this);		
		botonTodos.addListener(this);	
		
		labelRuta.setWidth("100");
		ruta.addComponent(labelRuta);
		campoRuta.setValue("");
		campoRuta.setWidth("300");
		ruta.addComponent(campoRuta);
		ruta.addComponent(checkbox);
		checkbox.setValue(true);
		ruta.addComponent(botonRuta);
		ruta.setSpacing(true);
		
		labelBusqueda.setWidth("100");
		busqueda.addComponent(labelBusqueda);
		campoBusqueda.setWidth("300");
		busqueda.addComponent(campoBusqueda);
		criterioBusqueda.setNullSelectionItemId(1);
		criterioBusqueda.setItemCaption(1,"Titulo");
		criterioBusqueda.addItem(2);
		criterioBusqueda.setItemCaption(2,"Artista");
		criterioBusqueda.addItem(3);
		criterioBusqueda.setItemCaption(3,"Álbum");
		criterioBusqueda.addItem(4);
		criterioBusqueda.setItemCaption(4,"Género");
		busqueda.addComponent(criterioBusqueda);	
		busqueda.addComponent(botonBusqueda);
		busqueda.addComponent(botonTodos);
		busqueda.addComponent(info);
		busqueda.setSpacing(true);
		
		v.addComponent(ruta);
		v.addComponent(busqueda);
		initTable(songs);
		v.addComponent(tabla);
		v.setSpacing(true);
		
		mainWindow.addComponent(v);
		setMainWindow(mainWindow);
	}
	
	public void initTable(ArrayList<QuerySong> sngs) {
		v.removeComponent(tabla);
		tabla = new Table();
		tabla.setSizeFull();
		tabla.setHeight("500");
		tabla.addContainerProperty("Artista",String.class,"-");
		tabla.addContainerProperty("Album",String.class,"-");
		tabla.addContainerProperty("Nº Pista",Integer.class,0);
		tabla.addContainerProperty("Año",String.class,"-");
		tabla.addContainerProperty("Género",String.class,"-");
		tabla.addContainerProperty("Titulo",String.class,"-");
		
		int i = 0;
		for (QuerySong s : sngs)
			tabla.addItem(new Object[] {s.getArtist(),
									    s.getAlbum(),
									    s.getTrackNumber(),
									    s.getYear(),
									    s.getGenre(),
									    s.getTitle()},
						  i++);
		v.addComponent(tabla);
	}
	
    public void buttonClick (Button.ClickEvent event) {
    	if (event.getButton() == botonBusqueda) {
    		String queryString;
    		
    		if (criterioBusqueda.getValue() == null)
     			queryString = (String)campoBusqueda.getValue();
    		else if ((Integer)criterioBusqueda.getValue() == 2)
    			queryString = "artist:\"" + (String)campoBusqueda.getValue() + "\"";
    		else if ((Integer)criterioBusqueda.getValue() == 3)
    			queryString = "album:\"" + (String)campoBusqueda.getValue() + "\"";
    		else if ((Integer)criterioBusqueda.getValue() == 4)
    			queryString = "genre:\"" + (String)campoBusqueda.getValue() + "\"";
    		else    		    		
    			queryString = (String)campoBusqueda.getValue();
    		
    	    SolrQuery query = new SolrQuery();
    	    query.setQuery(queryString);
    	    query.addSortField("artist",SolrQuery.ORDER.asc);
    	           	    
    	    try {
    	    	QueryResponse rsp = server.query(query);
    	    	SolrDocumentList docs = rsp.getResults();
    	    	querySongs = new ArrayList<QuerySong>();
        	    Iterator<SolrDocument> it = docs.iterator();
        	    while (it.hasNext())
        	    	querySongs.add(new QuerySong(it.next()));
        	    
        	    initTable(querySongs);
        		info.setVisible(true);
        		info.setCaption(docs.getNumFound() + " encontrados");
    	    } catch (Exception e) {}
    	} else if (event.getButton() == botonRuta) {
    		try {
    			if (checkbox.booleanValue())
    				scanner.scan((String)campoRuta.getValue(),true);
    			else
    				scanner.scan((String)campoRuta.getValue(),false);
        		scanner.toSOLR();
    		} catch (Exception e) {}
    		ArrayList<Song> sngs = scanner.getSongs();
    		songs = new ArrayList<QuerySong>();
    		for (Song s : sngs)
    			songs.add(new QuerySong(s));
    		initTable(songs);
    		info.setVisible(true);
    		info.setCaption(songs.size() + " encontrados");
    	} else {
    		initTable(songs);
    		info.setVisible(false);
    	}
    }
}
