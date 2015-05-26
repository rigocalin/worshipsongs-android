package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.service.AuthorListAdapterService;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Seenivasan on 5/17/2015.
 */
public class AuthorListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private AuthorDao authorDao;
    private AuthorSongDao authorSongDao;
    private List<Author> authors = new ArrayList<Author>();
    private List<String> authorsNames = new ArrayList<String>();
    private AuthorListAdapterService adapterService = new AuthorListAdapterService();
    private ArrayAdapter<String> adapter;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp() {
        authorDao = new AuthorDao(getActivity());
        authorSongDao = new AuthorSongDao(getActivity());
        loadAuthors();
    }

    private void loadAuthors() {
        authorDao.open();
        authorSongDao.open();
        List<AuthorSong> authorSongList = new ArrayList<AuthorSong>();
        authorSongList = authorSongDao.findAuthorsFromAuthorBooks();
        for (AuthorSong authorSong : authorSongList) {
            authors.add(authorDao.findAuthorByID(authorSong.getAuthorId()));
        }
        //authors = authorDao.findAll();
        for (Author author : authors) {
            if (!author.getDisplayName().toLowerCase().contains("unknown") && author.getDisplayName() != null) {
                authorsNames.add(author.getDisplayName());
            }
            Collections.sort(authorsNames);
            adapter = adapterService.getAuthorListAdapter(authorsNames, getFragmentManager());
            setListAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = new MenuInflater(getActivity().getApplicationContext());
        inflater.inflate(R.menu.default_action_bar_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconified(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = adapterService.getAuthorListAdapter(getFilteredAuthors(newText), getFragmentManager());
                setListAdapter(adapterService.getAuthorListAdapter(getFilteredAuthors(newText), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = adapterService.getAuthorListAdapter(getFilteredAuthors(query), getFragmentManager());
                setListAdapter(adapterService.getAuthorListAdapter(getFilteredAuthors(query), getFragmentManager()));
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        searchView.onActionViewCollapsed();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<String> getFilteredAuthors(String text) {
        List<String> filteredSongs = new ArrayList<String>();
        for (String author : authorsNames) {
            if (author.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(author);
            }
        }
        return filteredSongs;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQuery("", false);
        searchView.clearFocus();
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Log.d("On refresh in Song book list", "");
    }
}