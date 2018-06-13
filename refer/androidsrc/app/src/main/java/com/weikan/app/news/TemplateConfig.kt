package com.weikan.app.news

import com.weikan.app.R

/**
 * @author kailun on 16/11/20.
 */
class TemplateConfig {

    val layoutHome: Int
    val layoutNewsNone: Int
    val layoutNewsSingle: Int
    val layoutNewsMultiple: Int
    val drawablePlaceHolderNews: Int
    val drawablePlaceHolderBanner: Int
    val layoutNewsPicVideo: Int
    val layoutWxArticle: Int
    val layoutMultiArticle: Int

    private constructor(
            layoutHome: Int,
            layoutNewsNone: Int,
            layoutNewsSingle: Int,
            layoutNewsMultiple: Int,
            layoutNewsPicVideo: Int,
            layoutWxArticle: Int,
            layoutMultiArticle: Int,
            drawablePlaceHolderNews: Int,
            drawablePlaceHolderBanner: Int
    ) {
        this.layoutHome = layoutHome
        this.layoutNewsNone = layoutNewsNone
        this.layoutNewsSingle = layoutNewsSingle
        this.layoutNewsMultiple = layoutNewsMultiple
        this.drawablePlaceHolderNews = drawablePlaceHolderNews
        this.drawablePlaceHolderBanner = drawablePlaceHolderBanner
        this.layoutNewsPicVideo = layoutNewsPicVideo
        this.layoutWxArticle = layoutWxArticle
        this.layoutMultiArticle = layoutMultiArticle
    }

    companion object {
        var templateConfig: TemplateConfig? = null

        @JvmStatic
        fun initConfig() {

            templateConfig =  TemplateConfig(
                        R.layout.fragment_news_home,
                        R.layout.widget_news_none_view,
                        R.layout.widget_news_single_view,
                        R.layout.widget_news_multiple_view,
                        R.layout.widget_news_pic_video,
                        R.layout.original_main_list_item,
                        R.layout.item_shouye_wemoney,
                        R.drawable.placeholder_news,
                        R.drawable.placeholder_banner)
        }

        @JvmStatic
        fun getConfig(): TemplateConfig {
            return templateConfig!!
        }
    }

}
