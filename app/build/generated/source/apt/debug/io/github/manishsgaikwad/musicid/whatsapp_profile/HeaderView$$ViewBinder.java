// Generated code from Butter Knife. Do not modify!
package io.github.manishsgaikwad.musicid.whatsapp_profile;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HeaderView$$ViewBinder<T extends io.github.manishsgaikwad.musicid.whatsapp_profile.HeaderView> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558656, "field 'name'");
    target.name = finder.castView(view, 2131558656, "field 'name'");
    view = finder.findRequiredView(source, 2131558657, "field 'lastSeen'");
    target.lastSeen = finder.castView(view, 2131558657, "field 'lastSeen'");
  }

  @Override public void unbind(T target) {
    target.name = null;
    target.lastSeen = null;
  }
}
