package com.jiketuandui.antinetfraud.Activity.SettingActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.jiketuandui.antinetfraud.Adapter.ListContentAdapter;
import com.jiketuandui.antinetfraud.Bean.ArticleContent;
import com.jiketuandui.antinetfraud.Bean.HistoryArticle;
import com.jiketuandui.antinetfraud.Bean.ListContent;
import com.jiketuandui.antinetfraud.R;
import com.jiketuandui.antinetfraud.Service.NetBroadcastReceiver;
import com.jiketuandui.antinetfraud.Util.MyApplication;
import com.jiketuandui.antinetfraud.Util.NetWorkUtils;
import com.jiketuandui.antinetfraud.Util.SharedPManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailActivity extends AppCompatActivity implements NetBroadcastReceiver.netEventHandler {
    private MaterialRefreshLayout materialRefreshLayout;
    private ListContentAdapter mListContentAdapter;
    private List<ListContent> mListContents = new ArrayList<>();
    private boolean isFirstRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        // 注册
        NetBroadcastReceiver.mListeners.add(this);

        initView();
        initListener();
        inittagsback();
    }

    // 返回键
    private void inittagsback() {
        FrameLayout tagsback = (FrameLayout) findViewById(R.id.back);
        tagsback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 初始化响应事件
     */
    private void initListener() {
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                materialRefreshLayout.finishRefreshLoadMore();
                if (NetWorkUtils.isConnectNET(HistoryDetailActivity.this)) {
                    new initDataTask().execute();
                } else {
                    materialRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                materialRefreshLayout.finishRefresh();
                if (NetWorkUtils.isConnectNET(HistoryDetailActivity.this)) {
                    new LoadMoreDataTask().execute();
                } else {
                    materialRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        this.materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mListContentAdapter = new ListContentAdapter(HistoryDetailActivity.this, mListContents, true, 1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HistoryDetailActivity.this,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mListContentAdapter);

        // 如果是第一次刷新就启动一次刷新
        if (isFirstRefresh) {
            if (NetWorkUtils.isConnectNET(HistoryDetailActivity.this)) {
                materialRefreshLayout.autoRefresh();
                isFirstRefresh = false;
            } else {
                isFirstRefresh = true;
            }
        }
    }

    @Override
    public void onNetChange() {
        if (MyApplication.getInstance().getmNetWorkState() != NetWorkUtils.NET_TYPE_NO_NETWORK &&
                mListContents != null && mListContents.size() == 0) {
            materialRefreshLayout.autoRefresh();
        }
    }

    /**
     * 初始化数据
     */
    private class initDataTask extends AsyncTask<Void, Void, List<ListContent>> {

        @Override
        protected List<ListContent> doInBackground(Void... voids) {
            List<HistoryArticle> mHistoryContents =
                    ((MyApplication) getApplication()).instancepostAccount().getBrowserHistory(
                            new SharedPManager(HistoryDetailActivity.this)
                                    .getString(MyApplication.getInstance().getUid(), null));
            List<ListContent> mListContents = new ArrayList<>();
            ListContent listContent;
            if (mHistoryContents != null && mHistoryContents.size() != 0) {
                for (HistoryArticle historyArticle : mHistoryContents) {
                    ArticleContent articleContent = ((MyApplication) getApplication())
                            .instanceConnect()
                            .setArticleURL(Integer.valueOf(historyArticle.getArticle_id()));
                    if (articleContent != null) {
                        listContent = new ListContent();
                        listContent.setContent(articleContent.getContent());
                        listContent.setCreatetime(articleContent.getCreatetime());
                        listContent.setId(articleContent.getId());
                        listContent.setTitle(articleContent.getTitle());
                        listContent.setImagelink(articleContent.getImagelink());
                        listContent.setPraise(articleContent.getPraise());
                        listContent.setSource(articleContent.getSource());
                        listContent.setReading(articleContent.getReading());
                        listContent.setTagid(articleContent.getTagid());
                        mListContents.add(listContent);
                    }
                }
            }
            return mListContents;
        }

        @Override
        protected void onPostExecute(List<ListContent> mListContents) {
            super.onPostExecute(mListContents);
            if (mListContents != null) {
                mListContentAdapter.setData(mListContents);
                mListContentAdapter.notifyDataSetChanged();
            }
            materialRefreshLayout.finishRefresh();
        }
    }

    /**
     * 加载更多数据
     */
    private class LoadMoreDataTask extends AsyncTask<Void, Void, List<ListContent>> {

        @Override
        protected List<ListContent> doInBackground(Void... voids) {
            if (mListContentAdapter.getData().size() == 0) {
                return null;
            }
            // 这里需要添加执行代码
            return null;
        }

        @Override
        protected void onPostExecute(List<ListContent> ListContents) {
            super.onPostExecute(ListContents);
            if (ListContents == null) {
                materialRefreshLayout.finishRefreshLoadMore();
                return;
            }
            if (!MyApplication.getInstance().isContainLists(mListContentAdapter, ListContents)) {
                mListContentAdapter.addData(ListContents);
                mListContentAdapter.notifyDataSetChanged();
            }
            materialRefreshLayout.finishRefreshLoadMore();
        }
    }
}