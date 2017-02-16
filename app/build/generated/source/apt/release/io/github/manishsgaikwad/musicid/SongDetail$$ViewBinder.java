// Generated code from Butter Knife. Do not modify!
package io.github.manishsgaikwad.musicid;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SongDetail$$ViewBinder<T extends io.github.manishsgaikwad.musicid.SongDetail> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558548, "field 'toolbarHeaderView'");
    target.toolbarHeaderView = finder.castView(view, 2131558548, "field 'toolbarHeaderView'");
    view = finder.findRequiredView(source, 2131558549, "field 'floatHeaderView'");
    target.floatHeaderView = finder.castView(view, 2131558549, "field 'floatHeaderView'");
    view = finder.findRequiredView(source, 2131558544, "field 'appBarLayout'");
    target.appBarLayout = finder.castView(view, 2131558544, "field 'appBarLayout'");
    view = finder.findRequiredView(source, 2131558545, "field 'imageView'");
    target.imageView = finder.castView(view, 2131558545, "field 'imageView'");
    view = finder.findRequiredView(source, 2131558554, "field 'track_ET'");
    target.track_ET = finder.castView(view, 2131558554, "field 'track_ET'");
    view = finder.findRequiredView(source, 2131558555, "field 'album_ET'");
    target.album_ET = finder.castView(view, 2131558555, "field 'album_ET'");
    view = finder.findRequiredView(source, 2131558556, "field 'artist_ET'");
    target.artist_ET = finder.castView(view, 2131558556, "field 'artist_ET'");
    view = finder.findRequiredView(source, 2131558557, "field 'genre_ET'");
    target.genre_ET = finder.castView(view, 2131558557, "field 'genre_ET'");
    view = finder.findRequiredView(source, 2131558546, "field 'editCoverArt'");
    target.editCoverArt = finder.castView(view, 2131558546, "field 'editCoverArt'");
    view = finder.findRequiredView(source, 2131558550, "field 'scrollView'");
    target.scrollView = finder.castView(view, 2131558550, "field 'scrollView'");
    view = finder.findRequiredView(source, 2131558558, "field 'okEdit2'");
    target.okEdit2 = finder.castView(view, 2131558558, "field 'okEdit2'");
  }

  @Override public void unbind(T target) {
    target.toolbarHeaderView = null;
    target.floatHeaderView = null;
    target.appBarLayout = null;
    target.imageView = null;
    target.track_ET = null;
    target.album_ET = null;
    target.artist_ET = null;
    target.genre_ET = null;
    target.editCoverArt = null;
    target.scrollView = null;
    target.okEdit2 = null;
  }
}
