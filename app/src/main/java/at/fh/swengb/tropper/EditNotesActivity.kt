package at.fh.swengb.tropper

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_notes.*


class EditNotesActivity : AppCompatActivity() {

    companion object {
        val EXTRA_ADDED_OR_EDITED_RESULT = "ADD_OR_EDITED_RESULT"
        val TOKEN = "TOKEN"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId) {
            R.id.savenote -> {

                val extra: String? = intent.getStringExtra(NoteListActivity.NOTEID)
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                val token = sharedPreferences.getString(TOKEN, null)

                if (
                    (extra != null) &&
                    (token != null)&&
                    (add_edit_text.text.toString().isNotEmpty() || add_edit_title.text.toString().isNotEmpty()))
                {
                    val note = Note(extra, add_edit_title.text.toString(), add_edit_text.text.toString(), true)
                    NoteRepository.addNote(this, note)
                    NoteRepository.uploadNote(
                        token,
                        note,
                        success = {
                            NoteRepository.addNote(this, it)
                        },
                        error = {
                        })

                    val resultIntent = intent
                    resultIntent.putExtra(EXTRA_ADDED_OR_EDITED_RESULT, "ADDED")
                    Log.e("addNote", "added Note")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    Toast.makeText(this, this.getString(R.string.fill_message) , Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_notes)

        val extra: String? = intent.getStringExtra(NoteListActivity.NOTEID)

        if(extra != null){
            val note:Note? = NoteRepository.getNoteById(this, extra)
            if(note != null) {
                add_edit_title.setText(note.title)
                add_edit_text.setText(note.text)
            }
        }
    }
}
