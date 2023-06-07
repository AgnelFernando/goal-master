package com.goalmaster.plan.view.addtask

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import java.lang.ref.WeakReference

class QueryHandler(cr: ContentResolver) : AsyncQueryHandler(cr) {
    private var mListener: WeakReference<AsyncQueryListener>? = null

    override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri) {
        super.onInsertComplete(token, cookie, uri)
        mListener!!.get()!!.onCrInsertComplete(uri.lastPathSegment!!.toLong())
    }

    constructor(cr: ContentResolver, listener: AsyncQueryListener) : this(cr) {
        mListener = WeakReference(listener)
    }

    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
        super.onQueryComplete(token, cookie, cursor)
    }

    override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {
        super.onUpdateComplete(token, cookie, result)
        mListener!!.get()!!.onCrUpdateComplete()
    }
}