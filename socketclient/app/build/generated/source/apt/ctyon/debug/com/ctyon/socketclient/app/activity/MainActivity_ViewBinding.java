// Generated code from Butter Knife. Do not modify!
package com.ctyon.socketclient.app.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ctyon.socketclient.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  private View view2131230819;

  private View view2131230878;

  private View view2131230813;

  private View view2131230857;

  private View view2131230816;

  private View view2131230890;

  private View view2131230855;

  private View view2131230837;

  private View view2131230941;

  private View view2131230939;

  private View view2131230947;

  private View view2131230853;

  private View view2131230854;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.connect, "field 'connect' and method 'onViewClicked'");
    target.connect = Utils.castView(view, R.id.connect, "field 'connect'", Button.class);
    view2131230819 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.login, "field 'login' and method 'onViewClicked'");
    target.login = Utils.castView(view, R.id.login, "field 'login'", Button.class);
    view2131230878 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clear_log, "field 'clearLog' and method 'onViewClicked'");
    target.clearLog = Utils.castView(view, R.id.clear_log, "field 'clearLog'", Button.class);
    view2131230813 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.sendList = Utils.findRequiredViewAsType(source, R.id.send_list, "field 'sendList'", RecyclerView.class);
    target.receList = Utils.findRequiredViewAsType(source, R.id.rece_list, "field 'receList'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.heartbeat_c, "method 'onViewClicked'");
    view2131230857 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clocksync, "method 'onViewClicked'");
    view2131230816 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.newmessage, "method 'onViewClicked'");
    view2131230890 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getweather, "method 'onViewClicked'");
    view2131230855 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.electricity, "method 'onViewClicked'");
    view2131230837 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.setpcount, "method 'onViewClicked'");
    view2131230941 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.sendvoicess, "method 'onViewClicked'");
    view2131230939 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.shutdown, "method 'onViewClicked'");
    view2131230947 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getlocation, "method 'onViewClicked'");
    view2131230853 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getmodem, "method 'onViewClicked'");
    view2131230854 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.connect = null;
    target.login = null;
    target.clearLog = null;
    target.sendList = null;
    target.receList = null;

    view2131230819.setOnClickListener(null);
    view2131230819 = null;
    view2131230878.setOnClickListener(null);
    view2131230878 = null;
    view2131230813.setOnClickListener(null);
    view2131230813 = null;
    view2131230857.setOnClickListener(null);
    view2131230857 = null;
    view2131230816.setOnClickListener(null);
    view2131230816 = null;
    view2131230890.setOnClickListener(null);
    view2131230890 = null;
    view2131230855.setOnClickListener(null);
    view2131230855 = null;
    view2131230837.setOnClickListener(null);
    view2131230837 = null;
    view2131230941.setOnClickListener(null);
    view2131230941 = null;
    view2131230939.setOnClickListener(null);
    view2131230939 = null;
    view2131230947.setOnClickListener(null);
    view2131230947 = null;
    view2131230853.setOnClickListener(null);
    view2131230853 = null;
    view2131230854.setOnClickListener(null);
    view2131230854 = null;
  }
}
