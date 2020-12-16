package com.adni.memoapp.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.adni.memoapp.R
import com.adni.memoapp.data.models.Priority
import com.adni.memoapp.data.models.ToDoData
import com.adni.memoapp.data.viewmodels.ToDoViewModel
import com.adni.memoapp.databinding.FragmentUpdateBinding
import com.adni.memoapp.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_add.edt_title
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        binding?.spinnerPriorities.onItemSelectedListener = mSharedViewModel.listener


        setHasOptionsMenu(true)

        return binding?.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmDeleteItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDeleteItem() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete '${args.currentItem.title}'?")
            .setMessage("Are you sure want to remove '${args.currentItem.title}'?")
            .setPositiveButton("Yes"){ _, _ ->
                mToDoViewModel.deleteItem(args.currentItem)
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed: ${args.currentItem.title}",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No"){_,_ ->}
            .create()
            .show()
    }

    private fun updateItem() {
        val title = edt_title.text.toString()
        val description = edt_description.text.toString()
        val getPriority = spinner_priorities.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            //update current item
            val updateItem = ToDoData(
                args.currentItem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                description
            )
            mToDoViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show()
            // navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parsePriority(priority: Any): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
            else -> throw AssertionError()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}