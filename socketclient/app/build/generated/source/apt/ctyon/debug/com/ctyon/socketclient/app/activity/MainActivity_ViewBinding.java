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

  private View view2131230816;

  private View view2131230869;

  private View view2131230811;

  private View view2131230850;

  private View view2131230814;

  private View view2131230881;

  private View view2131230848;

  private View view2131230832;

  private View view2131230931;

  private View view2131230929;

  private View view2131230936;

  private View view2131230846;

  private View view2131230847;

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
    view2131230816 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.login, "field 'login' and method 'onViewClicked'");
    target.login = Utils.castView(view, R.id.login, "field 'login'", Button.class);
    view2131230869 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clear_log, "field 'clearLog' and method 'onViewClicked'");
    target.clearLog = Utils.castView(view, R.id.clear_log, "field 'clearLog'", Button.class);
    view2131230811 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.sendList = Utils.findRequiredViewAsType(source, R.id.send_list, "field 'sendList'", RecyclerView.class);
    target.receList = Utils.findRequiredViewAsType(source, R.id.rece_list, "field 'receList'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.heartbeat_c, "method 'onViewClicked'");
    view2131230850 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clocksync, "method 'onViewClicked'");
    view2131230814 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.newmessage, "method 'onViewClicked'");
    view2131230881 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getweather, "method 'onViewClicked'");
    view2131230848 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.electricity, "method 'onViewClicked'");
    view2131230832 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.setpcount, "method 'onViewClicked'");
    view2131230931 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.sendvoicess, "method 'onViewClicked'");
    view2131230929 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.shutdown, "method 'onViewClicked'");
    view2131230936 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getlocation, "method 'onViewClicked'");
    view2131230846 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getmodem, "method 'onViewClicked'");
    view2131230847 = view;
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

    view2131230816.setOnClickListener(null);
    view2131230816 = null;
    view2131230869.setOnClickListener(null);
    view2131230869 = null;
    view2131230811.setOnClickListener(null);
    view2131230811 = null;
    view2131230850.setOnClickListener(null);
    view2131230850 = null;
    view2131230814.setOnClickListener(null);
    view2131230814 = null;
    view2131230881.setOnClickListener(null);
    view2131230881 = null;
    view2131230848.setOnClickListener(null);
    view2131230848 = null;
    view2131230832.setOnClickListener(null);
    view2131230832 = null;
    view2131230931.setOnClickListener(null);
    view2131230931 = null;
    view2131230929.setOnClickListener(null);
    view2131230929 = null;
    view2131230936.setOnClickListener(null);
    view2131230936 = null;
    view2131230846.setOnClickListener(null);
    view2131230846 = null;
    view2131230847.setOnClickListener(null);
    view2131230847 = null;
  }
}
