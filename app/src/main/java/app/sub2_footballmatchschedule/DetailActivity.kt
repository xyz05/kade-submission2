package com.omrobbie.footballmatchschedule.mvp.detail

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import app.sub2_footballmatchschedule.DetailPresenter
import app.sub2_footballmatchschedule.DetailView
import app.sub2_footballmatchschedule.R
import app.sub2_footballmatchschedule.model.LiveItem
import app.sub2_footballmatchschedule.model.TeamsItem
import app.sub2_footballmatchschedule.network.ApiRepository
import app.sub2_footballmatchschedule.utils.DateTime
import app.sub2_footballmatchschedule.utils.invisible
import app.sub2_footballmatchschedule.utils.visible
import com.google.gson.Gson

import app.sub2_footballmatchschedule.utils.*
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*

const val INTENT_DETAIL = "INTENT_DETAIL"

class DetailActivity : AppCompatActivity(), DetailView {

    private lateinit var presenter: DetailPresenter

    private lateinit var progressBar: ProgressBar
    private lateinit var dataView: ScrollView

    private lateinit var data: LiveItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        data = intent.getParcelableExtra(INTENT_DETAIL)

        setupLayout(data)
        setupEnv(data)
    }

    override fun showLoading() {
        progressBar.visible()
        dataView.invisible()
    }

    override fun hideLoading() {
        progressBar.invisible()
        dataView.visible()
    }

    override fun showTeamDetails(dataHomeTeam: List<TeamsItem>, dataAwayTeam: List<TeamsItem>) {
        val imgHomeBadge = find<ImageView>(R.id.home_badge)
        Picasso.get()
                .load(dataHomeTeam[0].strTeamBadge)
                .into(imgHomeBadge)

        val imgAwayBadge = find<ImageView>(R.id.away_badge)
        Picasso.get()
                .load(dataAwayTeam[0].strTeamBadge)
                .into(imgAwayBadge)
    }

    private fun setupLayout(data: LiveItem) {
        relativeLayout {
            dataView = scrollView {
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    padding = dip(16)

                    // date
                    textCenter(DateTime.getLongDate(data.dateEvent))

                    // score
                    linearLayout {
                        gravity = Gravity.CENTER

                        textTitle(data.intHomeScore)
                        textTitle("vs")
                        textTitle(data.intAwayScore)
                    }

                    // team
                    linearLayout {
                        layoutTeamBadge(R.id.home_badge, data.strHomeTeam, data.strHomeFormation)
                                .lparams(matchParent, wrapContent, 1f)

                        layoutTeamBadge(R.id.away_badge, data.strAwayTeam, data.strAwayFormation)
                                .lparams(matchParent, wrapContent, 1f)
                    }

                    line()

                    layoutDetailItem("Goals", data.strHomeGoalDetails, data.strAwayGoalDetails)
                    layoutDetailItem("Shots", data.intHomeShots, data.intAwayShots)

                    line()

                    // lineups
                    textSubTitle("Lineups")

                    layoutDetailItem("Goal Keeper", data.strHomeLineupGoalkeeper, data.strAwayLineupGoalkeeper)
                    layoutDetailItem("Defense", data.strHomeLineupDefense, data.strAwayLineupDefense)
                    layoutDetailItem("Midfield", data.strHomeLineupMidfield, data.strAwayLineupMidfield)
                    layoutDetailItem("Forward", data.strHomeLineupForward, data.strAwayLineupForward)
                    layoutDetailItem("Substitutes", data.strHomeLineupSubstitutes, data.strAwayLineupSubstitutes)
                }
            }

            progressBar(R.id.progress_bar).lparams {
                centerInParent()
            }
        }
    }

    private fun setupEnv(data: LiveItem) {
        progressBar = find(R.id.progress_bar)

        presenter = DetailPresenter(this, ApiRepository(), Gson())
        presenter.getTeamDetails(data.idHomeTeam, data.idAwayTeam)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Match Detail"
    }
}
