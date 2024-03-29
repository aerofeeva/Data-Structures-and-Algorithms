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
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        ISet<URI> existingUris = new ChainedHashSet<URI>();
        for (Webpage pages : webpages) {
            existingUris.add(pages.getUri());
        }
        
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<URI, ISet<URI>>();
        for (Webpage page : webpages) {
            URI uriPage = page.getUri();
            IList<URI> allLinks = page.getLinks();
            ISet<URI> setofUri = new ChainedHashSet<URI>();
            for (URI link : allLinks) {
                if (existingUris.contains(link)) {
                setofUri.add(link); // need to omit the ones not in webpages though 
                }
            }
            graph.put(uriPage, setofUri);
        }
       return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        IDictionary<URI, Double> pageRank = new ChainedHashDictionary<URI, Double>();
        IDictionary<URI, Double> newPageRank = new ChainedHashDictionary<URI, Double>();
        for (KVPair<URI, ISet<URI>> pair : graph) {
            newPageRank.put(pair.getKey(), 1.0 / graph.size());
        }
        
        boolean flag = true;
        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            if (flag) {
                flag = false;
                for (KVPair<URI, Double> pair : newPageRank) {
                    pageRank.put(pair.getKey(), pair.getValue());
                    newPageRank.put(pair.getKey(), 0.0);
                }
                for (KVPair<URI, ISet<URI>> page : graph) {
                    double newRank = 0.0;
                    URI uriKey = page.getKey();
                    for (KVPair<URI, ISet<URI>> findPage : graph) {
                        URI currentUri = findPage.getKey();
                        if (findPage.getValue().contains(uriKey)) {
                            newRank += pageRank.get(currentUri) * decay / findPage.getValue().size();
                        }
                    }
                    if (page.getValue().size() == 0) {
                        double newValue = decay * pageRank.get(uriKey) / graph.size();
                        for (KVPair<URI, Double> pair : newPageRank) {
                            newPageRank.put(pair.getKey(), pair.getValue() + newValue); 
                        }   
                    }
                    newRank += (1 - decay) / graph.size() + newPageRank.get(uriKey);
                    if (Math.abs((newRank) - pageRank.get(uriKey)) > epsilon) {
                        flag = true;
                    }
                    newPageRank.put(uriKey, newRank);                   
                }
            } else {
                return pageRank;
            }
        }
        return pageRank;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return this.pageRanks.get(pageUri);

    }
}