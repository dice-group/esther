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

}
