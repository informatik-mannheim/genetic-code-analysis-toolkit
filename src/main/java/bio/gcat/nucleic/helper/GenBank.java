package bio.gcat.nucleic.helper;

import static bio.gcat.Utilities.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.http.client.utils.URIBuilder;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.loader.GenbankProxySequenceReader;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

public class GenBank {
	public static final String
	/*DATABASE_ALL = "gquery",
		DATABASE_ASSEMBLY = "assembly",
		DATABASE_BIOPROJECT = "bioproject",
		DATABASE_BIOSAMPLE = "biosample",
		DATABASE_BIOSYSTEMS = "biosystems",
		DATABASE_BOOKS = "books",
		DATABASE_CLINVAR = "clinvar",
		DATABASE_CLONE = "clone",
		DATABASE_CONSERVED_DOMAINS = "cdd",
		DATABASE_DBGAP = "gap",
		DATABASE_DBVAR = "dbvar",
		DATABASE_EPIGENOMICS = "epigenomics",
		DATABASE_EST = "nucest",
		DATABASE_GENE = "gene",
		DATABASE_GENOME = "genome",
		DATABASE_GEO_DATASETS = "gds",
		DATABASE_GEO_PROFILES = "geoprofiles",
		DATABASE_GSS = "nucgss",
		DATABASE_GTR = "gtr",
		DATABASE_HOMOLOGENE = "homologene",
		DATABASE_MEDGEN = "medgen",
		DATABASE_MESH = "mesh",
		DATABASE_NCBI_WEB_SITE = "ncbisearch",
		DATABASE_NLM_CATALOG = "nlmcatalog",*/
		DATABASE_NUCLEOTIDE = "nuccore",
	/*DATABASE_OMIM = "omim",
		DATABASE_PMC = "pmc",
		DATABASE_POPSET = "popset",
		DATABASE_PROBE = "probe",*/
		DATABASE_PROTEIN = "protein";
	/*DATABASE_PROTEIN_CLUSTERS = "proteinclusters",
		DATABASE_PUBCHEM_BIOASSAY = "pcassay",
		DATABASE_PUBCHEM_COMPOUND = "pccompound",
		DATABASE_PUBCHEM_SUBSTANCE = "pcsubstance",
		DATABASE_PUBMED = "pubmed",
		DATABASE_PUBMED_HEALTH = "pubmedhealth",
		DATABASE_SNP = "snp",
		DATABASE_SRA = "sra",
		DATABASE_STRUCTURE = "structure",
		DATABASE_TAXONOMY = "taxonomy",
		DATABASE_TOOLKIT = "toolkit",
		DATABASE_TOOLKITALL = "toolkitall",
		DATABASE_TOOLKITBOOK = "toolkitbook",
		DATABASE_UNIGENE = "unigene";*/
		
	private static final String
		EUTIL_BASE_URI = "http://eutils.ncbi.nlm.nih.gov",
		EUTIL_ENTREZ = "/entrez/eutils",
		EUTIL_SEARCH = "/esearch.fcgi",
		EUTIL_SUMMARY = "/esummary.fcgi";
	
	private static final String
		PARAMETER_DATABASE = "db",
		PARAMETER_RETMODE = "retmode",
		PARAMETER_RETSTART = "retstart",
		PARAMETER_RETMAX = "retmax",
		PARAMETER_TERM = "term",
		PARAMETER_ID = "id";
		
	@SuppressWarnings("unused") private static final String
		RETMODE_XML = "xml",
		RETMODE_JSON = "json";
	
	private static final String
		CHARSET = "UTF-8";
	
	public static List<String> search(String database, String term) throws URISyntaxException, IOException { return search(database, term, 0, 50); }
	public static List<String> search(String database, String term, int start, int max) throws URISyntaxException, IOException {
		URIBuilder builder = new URIBuilder(EUTIL_BASE_URI).setPath(EUTIL_ENTREZ+EUTIL_SEARCH)
			.addParameter(PARAMETER_DATABASE, database).addParameter(PARAMETER_TERM, term)
			.addParameter(PARAMETER_RETSTART, String.valueOf(start)).addParameter(PARAMETER_RETMAX, String.valueOf(max))
			.addParameter(PARAMETER_RETMODE, RETMODE_JSON);
		
		try(InputStream input = builder.build().toURL().openStream()) {
			return jsonArrayAsList(Json.parse(new InputStreamReader(input, CHARSET)).asObject()
				.get("esearchresult").asObject().get("idlist").asArray()).stream().map(object->object.toString()).collect(Collectors.toList());
		}
	}
	
	public static List<Map<String,Object>> summary(String database, List<String> ids) throws URISyntaxException, IOException {
		URIBuilder builder = new URIBuilder(EUTIL_BASE_URI).setPath(EUTIL_ENTREZ+EUTIL_SUMMARY)
			.addParameter(PARAMETER_DATABASE, database).addParameter(PARAMETER_ID, ids.stream().collect(Collectors.joining(",")))
			.addParameter(PARAMETER_RETMODE, RETMODE_JSON);
		
		try(InputStream input = builder.build().toURL().openStream()) {
			List<Map<String,Object>> summary = new ArrayList<>(ids.size());
			JsonObject results = Optional.ofNullable(Json.parse(new InputStreamReader(input, CHARSET)).asObject().get("result")).orElse(new JsonObject()).asObject();
			for(String name:results.names()) if(!"uids".equals(name))
				summary.add(jsonObjectAsMap(results.get(name).asObject()));
			return summary;
		}
	}
	
	public static DNASequence sequence(String accessionID) throws Exception {
		GenbankProxySequenceReader<NucleotideCompound> reader = new GenbankProxySequenceReader<NucleotideCompound>(createTempDirectory(GenBank.class.getSimpleName()).getAbsolutePath(), accessionID, DNACompoundSet.getDNACompoundSet());
		DNASequence sequence = new DNASequence(reader); reader.getHeaderParser().parseHeader(reader.getHeader(), sequence);
		return sequence;
	}
}