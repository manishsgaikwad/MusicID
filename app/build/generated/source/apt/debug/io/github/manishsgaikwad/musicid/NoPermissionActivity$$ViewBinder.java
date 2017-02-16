// Generated code from Butter Knife. Do not modify!
package io.github.manishsgaikwad.musicid;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class NoPermissionActivity$$ViewBinder<T extends io.github.manishsgaikwad.musicid.NoPermissionActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558553, "field 'launchSettings'");
    target.launchSettings = finder.castView(view, 2131558553, "field 'launchSettings'");
  }

  @Override public void unbind(T target) {
    target.launchSettings = null;
  }
}
