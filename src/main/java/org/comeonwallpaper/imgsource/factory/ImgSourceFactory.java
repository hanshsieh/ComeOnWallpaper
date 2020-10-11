package org.comeonwallpaper.imgsource.factory;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.conf.DataSource;
import org.comeonwallpaper.conf.SourceConf;
import org.comeonwallpaper.imgsource.ImgSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class ImgSourceFactory {
  protected final DataSource settings;
  protected final List<ImgSourceFactory> dependencies;

  public ImgSourceFactory(@NonNull DataSource settings, @NonNull List<ImgSourceFactory> dependencies) {
    this.settings = settings;
    this.dependencies = ImmutableList.copyOf(dependencies);
  }

  public static ImgSourceFactory fromConfig(@NonNull SourceConf conf)
      throws ClassNotFoundException,
      NoSuchMethodException,
      InvocationTargetException,
      IllegalAccessException,
      InstantiationException {
    List<ImgSourceFactory> dependFactories = new ArrayList<>(conf.dependencies.size());
    for (SourceConf dependConf : conf.dependencies) {
      dependFactories.add(fromConfig(dependConf));
    }
    ClassLoader classLoader = ImgSourceFactory.class.getClassLoader();
    Class<?> uncastedFactoryClass = Class.forName(conf.factoryClass, false, classLoader);
    // Ensure the class extends the factory class to prevent being cheated to instantiate a class that would cause
    // security vulnerability.
    Class<? extends ImgSourceFactory> factoryClass = uncastedFactoryClass.asSubclass(ImgSourceFactory.class);
    Constructor<? extends ImgSourceFactory> constructor = factoryClass.getConstructor(DataSource.class, List.class);
    return constructor.newInstance(DataSource.fromJson(conf.settings), dependFactories);
  }

  @NonNull
  public abstract ImgSource create() throws Exception;
}
