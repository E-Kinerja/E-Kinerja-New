package com.arya.e_kinerja.ui.tugas_aktivitas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arya.e_kinerja.R
import com.arya.e_kinerja.adapter.TugasAdapter
import com.arya.e_kinerja.data.Result
import com.arya.e_kinerja.databinding.FragmentTugasAktivitasBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TugasAktivitasFragment : Fragment() {

    private var _binding: FragmentTugasAktivitasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TugasAktivitasViewModel by viewModels()

    private lateinit var tugasAdapter: TugasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTugasAktivitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tugasAdapter = TugasAdapter()
        binding.rvTugasAktivitas.layoutManager = LinearLayoutManager(context)
        binding.rvTugasAktivitas.adapter = tugasAdapter

        binding.btnInputAktivitas.setOnClickListener {
            findNavController().navigate(
                R.id.action_tugasAktivitasFragment_to_inputAktivitasFragment
            )
        }

        var (currentBulan, currentTahun) = getCurrentDate()

        binding.edtTahun.setText(currentTahun.toString())
        binding.edtBulan.setText(
            resources.getStringArray(R.array.bulan)[currentBulan - 1].toString()
        )

        binding.edtBulan.setOnItemClickListener { _, _, position, _ ->
            currentBulan = position + 1

            getTugasAktivitas(currentBulan, currentTahun)
        }

        binding.edtTahun.setOnItemClickListener { adapterView, _, position, _ ->
            currentTahun = adapterView.adapter.getItem(position).toString().toInt()

            getTugasAktivitas(currentBulan, currentTahun)
        }
    }

    private fun getTugasAktivitas(bulan: Int, tahun: Int) {
        viewModel.getTugasAktivitas(bulan.toString(), tahun.toString()).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        tugasAdapter.submitList(result.data)
                    }
                    is Result.Error -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentDate(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()

        val formatTahun = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentTahun = formatTahun.format(calendar.time).toInt()

        val formatBulan = SimpleDateFormat("MM", Locale.getDefault())
        val currentBulan = formatBulan.format(calendar.time).toInt()

        return Pair(currentBulan, currentTahun)
    }

    override fun onResume() {
        super.onResume()

        val arrayBulan = resources.getStringArray(R.array.bulan)
        val arrayTahun = resources.getStringArray(R.array.tahun)

        val adapterBulan = ArrayAdapter(requireContext(), R.layout.item_dropdown, arrayBulan)
        val adapterTahun = ArrayAdapter(requireContext(), R.layout.item_dropdown, arrayTahun)

        binding.edtBulan.setAdapter(adapterBulan)
        binding.edtTahun.setAdapter(adapterTahun)

        getTugasAktivitas(
            resources.getStringArray(R.array.bulan).indexOf(
                binding.edtBulan.text.toString()
            ) + 1,
            binding.edtTahun.text.toString().toInt()
        )
    }
}