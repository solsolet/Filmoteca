package es.ua.eps.filmoteca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.core.util.size
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FilmListFragment : ListFragment() {
    var callback: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = FilmsFragmentAdapter(requireContext(), R.layout.item_peli, FilmDataSource.films)
        listAdapter = adapter

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val listView = listView
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        listView.setMultiChoiceModeListener(
            object : MultiChoiceModeListener {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater = mode.menuInflater
                    inflater.inflate(R.menu.film_list_contextual_menu, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.miDelete -> {
                            borrarItemsSeleccionados()
                            mode.finish()
                            true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode) {}
                override fun onItemCheckedStateChanged(mode: ActionMode, position: Int,
                                                       id: Long, checked: Boolean) {
                }
            })
        listView.onItemClickListener = OnItemClickListener { adapterView, view, position, l -> callback?.onItemSelected(position) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as OnItemSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " debe implementar OnItemSelectedListener")
        }
    }

    private fun borrarItemsSeleccionados() {
        val indices = listView.checkedItemPositions
        val toDelete =  mutableListOf<Film>()
        for (i in 0 until indices.size) {
            if (indices.valueAt(i)) {
                toDelete.add(FilmDataSource.films[indices.keyAt(i)])
            }
        }
        FilmDataSource.films.removeAll(toDelete)
        (listAdapter as FilmsFragmentAdapter).notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.film_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.miNewFilm -> {
                nuevaPelicula()
                return true
            }
            R.id.miShowAbout -> {
                abrirAcercaDe()
                return true
            }
            R.id.miCloseSession -> {
                signOut()
                return true
            }
            R.id.miDisconnect -> {
                disconnect()
                return true
            }
        }
        return false
    }

    private fun abrirAcercaDe() {
        val intent = Intent(activity, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun nuevaPelicula() {
        val f = Film()
        FilmDataSource.films.add(f)
        (listAdapter as FilmsFragmentAdapter).notifyDataSetChanged()
    }

    /**
     * Sign out: clears the local session.
     * The user can sign back in next time with the same or different account.
     */
    private fun signOut() {
        UserData.clear()
        // Go back to login screen
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * Disconnect: clears session AND tells Credential Manager to forget this app
     * so the user will be prompted to choose an account again next time.
     */
    private fun disconnect() {
        val credentialManager = CredentialManager.create(requireContext())
        // ClearCredentialStateRequest tells the system to revoke the stored session
        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(
                    androidx.credentials.ClearCredentialStateRequest()
                )
            } catch (e: Exception) {
                Log.e("FilmListFragment", "Error disconnecting: ${e.message}")
            } finally {
                // Always clear local data and go to login, even if the remote call failed
                UserData.clear()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}