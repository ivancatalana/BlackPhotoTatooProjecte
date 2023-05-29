package com.example.blackphototatoo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<List<Post>> searchResults;
    private MutableLiveData<Post> selectedPost = new MutableLiveData<>();

    public LiveData<List<Post>> getSearchResults(String searchTerm) {
        if (searchResults == null) {
            searchResults = new MutableLiveData<>();
        }

        // Realiza la búsqueda en la colección de posts y actualiza los resultados
        List<Post> results = performSearch(searchTerm);
        searchResults.setValue(results);

        return searchResults;
    }

    private List<Post> performSearch(String searchTerm) {
        // Implementa la lógica para buscar los posts coincidentes en tu colección de posts
        // y devuelve los resultados como una lista
        List<Post> results = new ArrayList<>();
        // ...

        return results;
    }
    public LiveData<Post> getSelectedPost() {
        return selectedPost;
    }

    public void setSelectedPost(Post post) {
        selectedPost.setValue(post);
    }
}
