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

  private View view2131230820;

  private View view2131230882;

  private View view2131230814;

  private View view2131230860;

  private View view2131230817;

  private View view2131230894;

  private View view2131230858;

  private View view2131230838;

  private View view2131230946;

  private View view2131230944;

  private View view2131230953;

  private View view2131230856;

  private View view2131230857;

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
    view2131230820 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.login, "field 'login' and method 'onViewClicked'");
    target.login = Utils.castView(view, R.id.login, "field 'login'", Button.class);
    view2131230882 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clear_log, "field 'clearLog' and method 'onViewClicked'");
    target.clearLog = Utils.castView(view, R.id.clear_log, "field 'clearLog'", Button.class);
    view2131230814 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.sendList = Utils.findRequiredViewAsType(source, R.id.send_list, "field 'sendList'", RecyclerView.class);
    target.receList = Utils.findRequiredViewAsType(source, R.id.rece_list, "field 'receList'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.heartbeat_c, "method 'onViewClicked'");
    view2131230860 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clocksync, "method 'onViewClicked'");
    view2131230817 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.newmessage, "method 'onViewClicked'");
    view2131230894 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getweather, "method 'onViewClicked'");
    view2131230858 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.electricity, "method 'onViewClicked'");
    view2131230838 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.setpcount, "method 'onViewClicked'");
    view2131230946 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.sendvoicess, "method 'onViewClicked'");
    view2131230944 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.shutdown, "method 'onViewClicked'");
    view2131230953 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getlocation, "method 'onViewClicked'");
    view2131230856 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getmodem, "method 'onViewClicked'");
    view2131230857 = view;
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

    view2131230820.setOnClickListener(null);
    view2131230820 = null;
    view2131230882.setOnClickListener(null);
    view2131230882 = null;
    view2131230814.setOnClickListener(null);
    view2131230814 = null;
    view2131230860.setOnClickListener(null);
    view2131230860 = null;
    view2131230817.setOnClickListener(null);
    view2131230817 = null;
    view2131230894.setOnClickListener(null);
    view2131230894 = null;
    view2131230858.setOnClickListener(null);
    view2131230858 = null;
    view2131230838.setOnClickListener(null);
    view2131230838 = null;
    view2131230946.setOnClickListener(null);
    view2131230946 = null;
    view2131230944.setOnClickListener(null);
    view2131230944 = null;
    view2131230953.setOnClickListener(null);
    view2131230953 = null;
    view2131230856.setOnClickListener(null);
    view2131230856 = null;
    view2131230857.setOnClickListener(null);
    view2131230857 = null;
  }
}
