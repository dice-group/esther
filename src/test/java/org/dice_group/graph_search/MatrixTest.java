package org.dice_group.graph_search;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.graph_search.modes.IrrelevantDR;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.graph_search.modes.NotDisjointDR;
import org.dice_group.graph_search.modes.StrictDR;
import org.dice_group.graph_search.modes.SubsumedDR;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MatrixTest {
	private final static String PREFIX_NS = "www.example.com";
	
	@Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { "graph2.n3", buildTestOntology() });
        testConfigs.add(new Object[] { "graph2.n3", buildTestOntology2() });

        return testConfigs;
    }

    private String graphFile;
    private OntModel ontology;
    
    public MatrixTest(String graphFile, OntModel ontology) {
        this.graphFile = graphFile;
        this.ontology = ontology;
    }

	@Test
	public void testPropAdjMatrix() {
		Model model = ModelFactory.createDefaultModel();
		model.read(graphFile);

		DictionaryHelper help = new DictionaryHelper();
		Dictionary dict = help.createDictionary(model);

		Matrix ndMat = new NotDisjointDR(ontology, dict);
		Matrix stricMat = new StrictDR(ontology, dict);
		Matrix subsMat = new SubsumedDR(ontology, dict);
		Matrix irrMat = new IrrelevantDR(ontology, dict);

		// full matrix should be set for IrrelevantDR
		for (int i = 0; i < irrMat.getEdgeAdjMatrix().length; i++) {
			if(irrMat.getEdgeAdjMatrix()[i].nextClearBit(0) < irrMat.getEdgeAdjMatrix().length)
				Assert.fail();
		}
	}	

	private static OntModel buildTestOntology2() {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		OntProperty b = ontModel.createOntProperty(PREFIX_NS + ":b");
		b.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		b.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));

		OntProperty d = ontModel.createOntProperty(PREFIX_NS + ":d");
		d.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c3"));
		d.addRange(ResourceFactory.createResource(PREFIX_NS + ":c4"));

		OntProperty m = ontModel.createOntProperty(PREFIX_NS + ":m");
		m.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c4"));
		m.addRange(ResourceFactory.createResource(PREFIX_NS + ":c5"));

		OntProperty g = ontModel.createOntProperty(PREFIX_NS + ":g");
		g.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		g.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));
		g.addRange(ResourceFactory.createResource(PREFIX_NS + ":c1"));

		OntProperty h = ontModel.createOntProperty(PREFIX_NS + ":h");
		h.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c3"));
		h.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		h.addRange(ResourceFactory.createResource(PREFIX_NS + ":c6"));

		OntProperty l = ontModel.createOntProperty(PREFIX_NS + ":l");
		l.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c4"));
		l.addRange(ResourceFactory.createResource(PREFIX_NS + ":c6"));

		OntProperty t = ontModel.createOntProperty(PREFIX_NS + ":t");
		t.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		t.addRange(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		t.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));

		OntProperty z = ontModel.createOntProperty(PREFIX_NS + ":z");
		z.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		z.addRange(ResourceFactory.createResource(PREFIX_NS + ":c5"));

		return ontModel;
	}

	private static OntModel buildTestOntology() {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		OntProperty b = ontModel.createOntProperty(PREFIX_NS + ":b");
		b.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		b.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));

		OntProperty d = ontModel.createOntProperty(PREFIX_NS + ":d");
		d.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c3"));
		d.addRange(ResourceFactory.createResource(PREFIX_NS + ":c4"));

		OntProperty m = ontModel.createOntProperty(PREFIX_NS + ":m");
		m.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c4"));
		m.addRange(ResourceFactory.createResource(PREFIX_NS + ":c5"));

		OntProperty g = ontModel.createOntProperty(PREFIX_NS + ":g");
		g.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		g.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));

		OntProperty h = ontModel.createOntProperty(PREFIX_NS + ":h");
		h.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c3"));
		h.addRange(ResourceFactory.createResource(PREFIX_NS + ":c6"));

		OntProperty l = ontModel.createOntProperty(PREFIX_NS + ":l");
		l.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c4"));
		l.addRange(ResourceFactory.createResource(PREFIX_NS + ":c6"));

		OntProperty t = ontModel.createOntProperty(PREFIX_NS + ":t");
		t.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		t.addRange(ResourceFactory.createResource(PREFIX_NS + ":c1"));

		OntProperty z = ontModel.createOntProperty(PREFIX_NS + ":z");
		z.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
		z.addRange(ResourceFactory.createResource(PREFIX_NS + ":c5"));

		return ontModel;
	}
}
