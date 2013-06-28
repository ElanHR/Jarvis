package org.elanhr.Jarvis;

import java.util.*;
import java.io.*;

//XMPP chat stuff
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


//Stanford NLP stuff

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;



public class Jarvis implements MessageListener{

	XMPPConnection connection;
	//StanfordCoreNLP pipeline;
	MaxentTagger tagger;
	public Jarvis(){
		
		Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    //pipeline = new StanfordCoreNLP(props);
		
	    
	     tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
	}
	
	String getResponse(String msg){
		// read some text in the text variable
		String response = "";
		
		response = tagger.tagString(msg);
		
		/*
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(msg);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);      
	        
	        response = response +"["+ word + "|" + pos +"|" +ne + "]  ";
	      }

	      // this is the parse tree of the current sentence
	      Tree tree = sentence.get(TreeAnnotation.class);

	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = 
	      document.get(CorefChainAnnotation.class);
	    
	    */
		
		
		return response;
	}
	
	public void login(String userName, String password) throws XMPPException
	{
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		connection = new XMPPConnection(config);

		connection.connect();
		connection.login(userName, password);
	}
	
	public void sendMessage(String message, String to) throws XMPPException
	{
		Chat chat = connection.getChatManager().createChat(to, this);
		chat.sendMessage(message);
	}
	
	public void displayBuddyList()
	{
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		
		System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for(RosterEntry r:entries)
		{
			System.out.println(r.getUser());
		}
	}

	public void disconnect()
	{
		connection.disconnect();
	}
	
	public void processMessage(Chat chat, Message message) 
	{
		if(message.getBody() == null){
			 System.out.println(chat.getParticipant() + " is typing");
		}else if(message.getType() == Message.Type.chat){
	        System.out.println(chat.getParticipant() + " says: " + message.getBody());
	        
	        
	        try {
				this.sendMessage("echo: " + getResponse(message.getBody()), chat.getParticipant());
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
    }
	
	public static void main(String args[]) throws XMPPException, IOException
	{
		// declare variables
		Jarvis c = new Jarvis();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg;


		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = true;


		// provide your login information here
		c.login("your.email@gmail.com", "your.password");


		c.displayBuddyList();
		System.out.println("-----");
		System.out.println("Enter your message in the console.");
		//System.out.println("All messages will be sent to abhijeet.maharana");
		System.out.println("-----\n");
		c.sendMessage("person@gmail.com", "hello!" );
		while( !(msg=br.readLine()).equals("bye"))
		{
			// your buddy's gmail address goes here
			c.sendMessage(msg, "person@gmail.com");
		}

		c.disconnect();
		System.exit(0);
	}

	
}


