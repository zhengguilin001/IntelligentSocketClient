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

  private View view2131230823;

  private View view2131230885;

  private View view2131230816;

  private View view2131230863;

  private View view2131230819;

  private View view2131230897;

  private View view2131230861;

  private View view2131230841;

  private View view2131230950;

  private View view2131230948;

  private View view2131230957;

  private View view2131230859;

  private View view2131230860;

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
    view2131230823 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.login, "field 'login' and method 'onViewClicked'");
    target.login = Utils.castView(view, R.id.login, "field 'login'", Button.class);
    view2131230885 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clear_log, "field 'clearLog' and method 'onViewClicked'");
    target.clearLog = Utils.castView(view, R.id.clear_log, "field 'clearLog'", Button.class);
    view2131230816 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.sendList = Utils.findRequiredViewAsType(source, R.id.send_list, "field 'sendList'", RecyclerView.class);
    target.receList = Utils.findRequiredViewAsType(source, R.id.rece_list, "field 'receList'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.heartbeat_c, "method 'onViewClicked'");
    view2131230863 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.clocksync, "method 'onViewClicked'");
    view2131230819 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.newmessage, "method 'onViewClicked'");
    view2131230897 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getweather, "method 'onViewClicked'");
    view2131230861 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.electricity, "method 'onViewClicked'");
    view2131230841 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.setpcount, "method 'onViewClicked'");
    view2131230950 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.sendvoicess, "method 'onViewClicked'");
    view2131230948 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.shutdown, "method 'onViewClicked'");
    view2131230957 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getlocation, "method 'onViewClicked'");
    view2131230859 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.getmodem, "method 'onViewClicked'");
    view2131230860 = view;
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

    view2131230823.setOnClickListener(null);
    view2131230823 = null;
    view2131230885.setOnClickListener(null);
    view2131230885 = null;
    view2131230816.setOnClickListener(null);
    view2131230816 = null;
    view2131230863.setOnClickListener(null);
    view2131230863 = null;
    view2131230819.setOnClickListener(null);
    view2131230819 = null;
    view2131230897.setOnClickListener(null);
    view2131230897 = null;
    view2131230861.setOnClickListener(null);
    view2131230861 = null;
    view2131230841.setOnClickListener(null);
    view2131230841 = null;
    view2131230950.setOnClickListener(null);
    view2131230950 = null;
    view2131230948.setOnClickListener(null);
    view2131230948 = null;
    view2131230957.setOnClickListener(null);
    view2131230957 = null;
    view2131230859.setOnClickListener(null);
    view2131230859 = null;
    view2131230860.setOnClickListener(null);
    view2131230860 = null;
  }
}
