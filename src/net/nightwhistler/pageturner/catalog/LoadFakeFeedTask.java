/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package net.nightwhistler.pageturner.catalog;

import android.content.Context;
import android.os.AsyncTask;
import com.google.inject.Inject;
import net.nightwhistler.nucular.atom.Link;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class LoadFakeFeedTask extends AsyncTask<Link, Integer, Void> {

    private LoadFeedCallback callback;

    private static final Logger LOG = LoggerFactory.getLogger("LoadFakeFeedTask");

    private Context context;
    private HttpClient client;

    private String baseURL;

    @Inject
    public LoadFakeFeedTask(Context context, HttpClient httpClient) {
        this.context = context;
        this.client = httpClient;
    }


    public void setCallback( LoadFeedCallback callback ) {
        this.callback = callback;
    }

    public void setBaseURL( String baseURL ) {
        this.baseURL = baseURL;
    }

    @Override
    protected void onPreExecute() {
        callback.onLoadingStart();
    }

    @Override
    protected Void doInBackground(Link... params) {

        Link imageLink = params[0];

        if ( imageLink == null ) {
            return null;
        }

        try {
            String href = imageLink.getHref();
            String target = new URL(new URL(baseURL), href).toString();

            LOG.info("Downloading image: " + target);

            HttpResponse resp = client.execute(new HttpGet(target));

            imageLink.setBinData(EntityUtils.toByteArray(resp.getEntity()));

        } catch (Exception io) {
            LOG.error("Could not load image: ", io);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        callback.notifyLinkUpdated();
        callback.onLoadingDone();
    }
}

