package cz.johrusk.showsmscode.fragment

import android.app.Fragment
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.johrusk.showsmscode.R
import kotlinx.android.synthetic.main.main_activity.*

/**
 * Created by Pepa on 29.06.2016.
 */

class MainActivityKotlinFragment : Fragment() {

    companion object
    {


    }


    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {

        return inflater?.inflate(R.layout.main_activity_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

//        Text underlinig
        MA_tv_addToGit.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_author.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_sourceCode.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_reportIssue.paintFlags += Paint.UNDERLINE_TEXT_FLAG
    }
}
