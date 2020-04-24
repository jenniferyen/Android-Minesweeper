package com.example.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import model.MinesweeperModel
import model.MinesweeperModel.FLAGGING
import model.MinesweeperModel.NOTFLAGGING

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // restart button
        btnRestart.setOnClickListener {
            minesweeperView.restart()
        }

        // mode toggling
        switchFlag.setOnCheckedChangeListener { _, toggleOn ->
            if (toggleOn) {
                MinesweeperModel.mode = FLAGGING
            } else {
                MinesweeperModel.mode = NOTFLAGGING
            }
        }
    }

    fun showNumFlags(numFlags: Int) {
        tvFlags.text = getString(R.string.num_flags, numFlags.toString())
    }

    fun showSnackbar(msg: String) {
        Snackbar.make(minesweeperView, msg, Snackbar.LENGTH_LONG).show()
    }


}
