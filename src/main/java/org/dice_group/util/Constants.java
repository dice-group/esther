package org.dice_group.util;

public interface Constants {
	String SUB_TYPE 	= "SUB";
	String OBJ_TYPE 	= "OBJ";
	String SO_TYPE 		= "SUB-OBJ";
	String OS_TYPE 		= "OBJ-SUB";

	String SUB_QUERY 	= "select * where { ?n ?p1 ?q . ?n ?p2 ?n2 . filter (?p1!=?p2 || ?q!=?n2). } ";
	String OBJ_QUERY 	= "select * where { ?q ?p1 ?n . ?n2 ?p2 ?n . filter (?p1!=?p2 || ?q!=?n2). } ";
	String SO_QUERY 	= "select * where { ?n ?p1 ?q . ?n2 ?p2 ?n . filter (?p1!=?p2 || ?q!=?n2). } ";
	String OS_QUERY 	= "select * where { ?n ?p1 ?q . ?q ?p2 ?n2 . filter (?p1!=?p2 || ?q!=?n2). } ";

	String ENT_DICT_FILE 	= "entities.dict";
	String REL_DICT_FILE 	= "relations.dict";
	
	String ENT_EMB_FILE 	= "entity_embedding.csv";
	String REL_EMB_FILE 	= "relation_embedding.csv";
	
	String REL_W_FILE 		= "relation_w.csv";
	String REL_X_FILE 		= "relation_x.csv";
	String REL_Y_FILE 		= "relation_y.csv";
	String REL_Z_FILE 		= "relation_z.csv";
	
	
	String DENSE_STRING 	= "DensE";
	String ROTATE_STRING 	= "RotatE";
	String TRANSE_STRING 	= "TransE";
	
}
