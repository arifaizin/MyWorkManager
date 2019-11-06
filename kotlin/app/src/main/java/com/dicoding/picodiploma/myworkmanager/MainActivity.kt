package com.dicoding.picodiploma.myworkmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import androidx.work.Data.Builder
import com.dicoding.picodiploma.myworkmanager.R.layout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        btnOneTimeTask.setOnClickListener {
            textStatus.text = "Status :"
            val data = Builder()
                    .putString(MyWorker.EXTRA_CITY, editCity.text.toString())
                    .build()
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueue(oneTimeWorkRequest)
            WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observe(this@MainActivity, object : Observer<WorkInfo> {
                override fun onChanged(workInfo: WorkInfo) {
                    val status = workInfo.state.name
                    textStatus.append("\n" + status)
                }
            })
        }

        btnPeriodicTask.setOnClickListener {
            textStatus.text = "Status :"
            val data = Builder()
                    .putString(MyWorker.EXTRA_CITY, editCity.text.toString())
                    .build()
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueue(periodicWorkRequest)
            WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this@MainActivity, object : Observer<WorkInfo> {
                override fun onChanged(workInfo: WorkInfo) {
                    val status = workInfo.state.name
                    textStatus.append("\n" + status)
                    btnCancelTask.isEnabled = false
                    if (workInfo.state == WorkInfo.State.ENQUEUED){
                        btnCancelTask.isEnabled = true
                    }
                }
            })
        }
        btnCancelTask.setOnClickListener { WorkManager.getInstance().cancelWorkById(periodicWorkRequest.id) }
    }
}