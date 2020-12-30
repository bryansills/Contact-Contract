package ninja.bryansills.contactcontract

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ninja.bryansills.contactcontract.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val selectSuspect = registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        parseContactSelection(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.isEnabled = selectSuspect.canLaunch(this, null)
        binding.button.setOnClickListener {
            selectSuspect.launch(null)
        }
    }


    private fun parseContactSelection(contactUri: Uri) {
//        contactUri?.let {
            // Specify which fields you want your query to return values for.
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            // Perform your query - the contactUri is like a "where" clause here
            val cursor = contentResolver.query(contactUri, queryFields, null, null, null)
            cursor?.use {
                // Verify cursor contains at least one result
                if (it.count == 0) {
                    return
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                it.moveToFirst()
                val suspect = it.getString(0)
                binding.button.text = suspect
            }
//        }
    }
}

fun <Input> ActivityResultContract<Input, *>.canLaunch(context: Context, input: Input): Boolean {
    val intent = this.createIntent(context, input)
    val packageManager = context.packageManager
    val resolvedActivity = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolvedActivity.isNotEmpty()
}

fun <Input> ActivityResultLauncher<Input>.canLaunch(context: Context, input: Input): Boolean {
    val intent = contract.createIntent(context, input)
    val packageManager = context.packageManager
    val resolvedActivity = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolvedActivity.isNotEmpty()
}
