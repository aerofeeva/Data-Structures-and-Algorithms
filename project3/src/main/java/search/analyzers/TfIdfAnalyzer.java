package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    private IDictionary<URI, Double> normDocumentVectors;

    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        this.normDocumentVectors = new ChainedHashDictionary<URI, Double>();
        for (KVPair<URI, IDictionary<String, Double>> pair : documentTfIdfVectors) {
            double normVal = norm(pair.getValue());
            this.normDocumentVectors.put(pair.getKey(), normVal);
        }
    }
    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Integer> freq = new ChainedHashDictionary<String, Integer>();
        IDictionary<String, Double> idfDictionary = new ChainedHashDictionary<String, Double>();
        for (Webpage page : pages) {
            ISet<String> set = new ChainedHashSet<String>();
            IList<String> allWords = page.getWords();
            for (String word : allWords) {
                set.add(word);
            }
            for (String uniqueWord : set) {
                if (!freq.containsKey(uniqueWord)) {
                    freq.put(uniqueWord,  1);
                } else {
                    freq.put(uniqueWord, (freq.get(uniqueWord) + 1));
                }
            }
        }
        for (KVPair<String, Integer> pair : freq) {
            double newVal = Math.log(1.0 * pages.size() / pair.getValue());
            idfDictionary.put(pair.getKey(), newVal);
        }
        return idfDictionary;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        IDictionary<String, Integer> freq = new ChainedHashDictionary<String, Integer>();
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<String, Double>();
        for (String word : words) {
            if (!freq.containsKey(word)) {
                freq.put(word, 1);
            } else {
                freq.put(word, freq.get(word) + 1);
            }
        } 
        for (KVPair<String, Integer> pair : freq) {
            double val = 1.0 * pair.getValue() / words.size();
            tfScores.put(pair.getKey(), val);
        }
        return tfScores;
    }

            
    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        IDictionary<URI, IDictionary<String, Double>> tfIdfVectors = new ChainedHashDictionary<URI, 
                                                                         IDictionary<String, Double>>();
        for (Webpage page : pages) {
            IDictionary<String, Double> tfIdfScores = new ChainedHashDictionary<String, Double>();
            IList<String> words = page.getWords();
            IDictionary<String, Double> tfScores = computeTfScores(words);
            for (String word : words) {
                double score = tfScores.get(word) * this.idfScores.get(word);
                tfIdfScores.put(word, score);
                tfIdfVectors.put(page.getUri(), tfIdfScores);
            }
        }
        return tfIdfVectors;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.
        IDictionary<String, Double> documentVector = documentTfIdfVectors.get(pageUri);
        IDictionary<String, Double> queryVector = new ChainedHashDictionary<String, Double>(); //???

        double numerator = 0.0;
        IDictionary<String, Double> tfScores = computeTfScores(query);
        for (String word: query) {
            double tf = tfScores.get(word); // how frequently word appears in query
            double idf = idfScores.get(word);
            queryVector.put(word, (tf * idf));
            double docWordScore = 0.0;
            if (documentVector.containsKey(word)) {
                docWordScore = documentVector.get(word);
            }
            double queryWordScore = queryVector.get(word);
            numerator += docWordScore * queryWordScore; 
        }
        double denominator = this.normDocumentVectors.get(pageUri) * norm(queryVector);
        if (denominator != 0) {
            return numerator / denominator;
        }
        return 0.0;
    }

    private double norm(IDictionary<String, Double> vector) {
        double output = 0.0;
        for (KVPair<String, Double> pair: vector) {
            double score = pair.getValue();
            output += score * score;
        }
        return Math.sqrt(output);
    }
}
