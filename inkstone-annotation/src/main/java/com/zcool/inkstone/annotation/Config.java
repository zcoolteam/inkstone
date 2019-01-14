package com.zcool.inkstone.annotation;

import java.util.HashSet;
import java.util.Set;

public class Config {

    private final Set<ApplicationDelegate> mApplicationDelegates = new HashSet<>();
    private final Set<ServicesProvider> mServicesProviders = new HashSet<>();

    public void add(Config config) {
        this.mApplicationDelegates.addAll(config.mApplicationDelegates);
        this.mServicesProviders.addAll(config.mServicesProviders);
    }

    public void addApplicationDelegate(ApplicationDelegate applicationDelegate) {
        this.mApplicationDelegates.add(applicationDelegate);
    }

    public void addServicesProvider(ServicesProvider servicesProvider) {
        this.mServicesProviders.add(servicesProvider);
    }

    public Set<ApplicationDelegate> getApplicationDelegates() {
        return mApplicationDelegates;
    }

    public Set<ServicesProvider> getServicesProviders() {
        return mServicesProviders;
    }

    public static class ApplicationDelegate {
        public String clazz;
        public int priority;

        public static ApplicationDelegate valueOf(String clazz, int priority) {
            ApplicationDelegate target = new ApplicationDelegate();
            target.clazz = clazz;
            target.priority = priority;
            return target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApplicationDelegate that = (ApplicationDelegate) o;

            return clazz != null ? clazz.equals(that.clazz) : that.clazz == null;
        }

        @Override
        public int hashCode() {
            return clazz != null ? clazz.hashCode() : 0;
        }
    }

    public static class ServicesProvider {
        public String clazz;
        public int priority;

        public static ServicesProvider valueOf(String clazz, int priority) {
            ServicesProvider target = new ServicesProvider();
            target.clazz = clazz;
            target.priority = priority;
            return target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServicesProvider that = (ServicesProvider) o;

            return clazz != null ? clazz.equals(that.clazz) : that.clazz == null;
        }

        @Override
        public int hashCode() {
            return clazz != null ? clazz.hashCode() : 0;
        }
    }

}
