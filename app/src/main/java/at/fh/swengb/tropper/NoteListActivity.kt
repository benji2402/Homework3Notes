package at.fh.swengb.tropper

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.util.*


class NoteListActivity : AppCompatActivity() {
    companion object{
        val TOKEN = "TOKEN"
        val LASTSYNC = "LASTSYNC"
        val NOTEID = "NOTEID"
        val EXTRA_ADDED_OR_EDITED_RESULT = 0
    }
    val noteAdapter = NoteAdapter(){
        val intent = Intent(this, EditNotesActivity::class.java)
        intent.putExtra(NOTEID, it.id)
        startActivityForResult(intent, EXTRA_ADDED_OR_EDITED_RESULT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        noteSync()
    }


    override fun onResume() {
        super.onResume()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId) {
            R.id.sync_notes -> {
                noteSync()
                true
            }
            R.id.logout -> {
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                NoteRepository.clearDb(this)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true}
            R.id.addnote -> {
                val uuidString = UUID.randomUUID().toString()
                val intent = Intent(this, EditNotesActivity::class.java)
                intent.putExtra(NOTEID, uuidString)
                startActivityForResult(intent, EXTRA_ADDED_OR_EDITED_RESULT)
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EXTRA_ADDED_OR_EDITED_RESULT  && resultCode == Activity.RESULT_OK){
            noteSync()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    private fun noteSync() {

        note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
        note_recycler_view.adapter = noteAdapter

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(TOKEN, null)
        val lastSync = sharedPreferences.getLong(LASTSYNC, 0)

        if (accessToken != null){

            NoteRepository.getNotes(
                accessToken,
                lastSync,
                success = {

                    it.notes.map {NoteRepository.addNote(this, it) }
                    sharedPreferences.edit().putLong(LASTSYNC, it.lastSync).apply()
                    noteAdapter.updateList(NoteRepository.getNotesAll(this))
                },
                error = {
                    noteAdapter.updateList(NoteRepository.getNotesAll(this))
                    Toast.makeText(this, getString(R.string.note_sync_failed) , Toast.LENGTH_LONG).show()
                }
            )
        }
    }

}
