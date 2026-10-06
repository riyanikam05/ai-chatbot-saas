package com.riya.aichatbot.ai.dto;

import java.util.List;
import java.util.Map;

public class ChromaQueryRequest {

    private List<List<Double>> query_embeddings;

    private int n_results;

    private List<String> include;

    private Map<String, Object> where;

    public ChromaQueryRequest() {
    }

    public ChromaQueryRequest(
            List<List<Double>> queryEmbeddings,
            int nResults,
            List<String> include,
            Map<String, Object> where) {

        this.query_embeddings = queryEmbeddings;
        this.n_results = nResults;
        this.include = include;
        this.where = where;
    }

    public List<List<Double>> getQuery_embeddings() {
        return query_embeddings;
    }

    public void setQuery_embeddings(List<List<Double>> query_embeddings) {
        this.query_embeddings = query_embeddings;
    }

    public int getN_results() {
        return n_results;
    }

    public void setN_results(int n_results) {
        this.n_results = n_results;
    }

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    public Map<String, Object> getWhere() {
        return where;
    }

    public void setWhere(Map<String, Object> where) {
        this.where = where;
    }
}