package com.wisal.android.paging.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.wisal.android.paging.R
import com.wisal.android.paging.adapters.CharactersAdapter
import com.wisal.android.paging.databinding.ProviderTestingFragmentBinding
import com.wisal.android.paging.models.Character
import com.wisal.android.paging.models.Gender
import com.wisal.android.paging.models.NameUrl
import com.wisal.android.paging.models.Status
import com.wisal.android.paging.provider.RickAndMortyApiContract
import com.wisal.android.paging.provider.RickAndMortyApiContract.CHARACTERS_CONTENT_PATH
import com.wisal.android.paging.provider.RickAndMortyApiContract.CHARACTERS_CONTENT_URI
import com.wisal.android.paging.viewmodels.UiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ProviderTestingFragment : Fragment() {

    private val TAG = "ContentProviderTestingF"

    @Inject lateinit var adapter: CharactersAdapter
    private lateinit var binding: ProviderTestingFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ProviderTestingFragmentBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val mCursor = context?.contentResolver?.query(
                CHARACTERS_CONTENT_URI,
                arrayOf(CHARACTERS_CONTENT_PATH),
                null,
                null,
                null
            )

            if(mCursor != null) {
                with(mCursor) {
                    val list: MutableList<Character> = mutableListOf()
                    while (moveToNext()) {
                        val id  = mCursor.getString(mCursor.getColumnIndexOrThrow(RickAndMortyApiContract.CharactersTable.Columns.KEY_CHARACTER_ID))
                        val name = mCursor.getString(mCursor.getColumnIndexOrThrow(RickAndMortyApiContract.CharactersTable.Columns.KEY_CHARACTER_NAME))
                        val status = mCursor.getString(mCursor.getColumnIndexOrThrow(RickAndMortyApiContract.CharactersTable.Columns.KEY_CHARACTER_STATUS))
                        val gender = mCursor.getString(mCursor.getColumnIndexOrThrow(RickAndMortyApiContract.CharactersTable.Columns.KEY_CHARACTER_GENDER))
                        val image = mCursor.getString(mCursor.getColumnIndexOrThrow(RickAndMortyApiContract.CharactersTable.Columns.KEY_CHARACTER_IMAGE))

                        val character = Character(
                            id = id.toInt(),
                            name = name,
                            status = if(status == Status.ALIVE.name) Status.ALIVE else if (status == Status.DEAD.name) Status.DEAD else Status.UNKNOWN,
                            gender = if(gender == Gender.FEMALE.name) Gender.FEMALE else if(gender == Gender.MALE.name) Gender.MALE else if(gender == Gender.UNKNOWN.name) Gender.UNKNOWN else Gender.GENDERLESS,
                            image = image,
                            species = "",
                            type = "",
                            url = "",
                            location = NameUrl("",""),
                            episode = listOf(),
                            created = "",
                            origin = NameUrl("","")
                        )
                        Log.d(TAG,"Each character ${character}")
                        list.add(character)
                    }
                    val uiModelList = list.map { character -> UiModel.CharacterItem(character) }
                    withContext(Dispatchers.Main) {
                        adapter.submitData(PagingData.from(uiModelList))
                    }
                }
            } else Log.e(TAG,"Error cursor is null")
        }

    }

}