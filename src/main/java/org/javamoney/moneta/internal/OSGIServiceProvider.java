/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.javamoney.moneta.internal;

import org.javamoney.moneta.spi.PriorityServiceComparator;
import org.osgi.framework.*;

import javax.money.spi.ServiceProvider;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServiceContext implementation based on OSGI Service mechanisms.
 */
public class OSGIServiceProvider implements ServiceProvider{

    private static final Logger LOG = Logger.getLogger(OSGIServiceProvider.class.getName());
    private static final OSGIServiceComparator REF_COMPARATOR = new OSGIServiceComparator();

    private BundleContext bundleContext;

    public OSGIServiceProvider( BundleContext bundleContext){
        this.bundleContext = bundleContext;
    }

    public boolean isInitialized(){
        return true;
    }


    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        LOG.finest("TAMAYA  Loading service: " + serviceType.getName());
        ServiceReference<T> ref = this.bundleContext.getServiceReference(serviceType);
        if(ref!=null){
            return this.bundleContext.getService(ref);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> serviceType) {
        LOG.finest("TAMAYA  Creating service: " + serviceType.getName());
        ServiceReference<T> ref = this.bundleContext.getServiceReference(serviceType);
        if(ref!=null){
            try {
                return (T)this.bundleContext.getService(ref).getClass().newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType) {
        LOG.finest("TAMAYA  Loading services: " + serviceType.getName());
        List<ServiceReference<T>> refs = new ArrayList<>();
        List<T> services = new ArrayList<>(refs.size());
        try {
            refs.addAll(this.bundleContext.getServiceReferences(serviceType, null));
            Collections.sort(refs, REF_COMPARATOR);
            for(ServiceReference<T> ref:refs){
                T service = bundleContext.getService(ref);
                if(service!=null) {
                    services.add(service);
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        try{
            for(T service:ServiceLoader.load(serviceType)){
                services.add(service);
            }
            return services;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    public Enumeration<URL> getResources(String resource, ClassLoader cl) throws IOException{
        LOG.finest("TAMAYA  Loading resources: " + resource);
        List<URL> result = new ArrayList<>();
        URL url = bundleContext.getBundle()
                .getEntry(resource);
        if(url != null) {
            LOG.finest("TAMAYA  Resource: " + resource + " found in unregistered bundle " +
                    bundleContext.getBundle().getSymbolicName());
            result.add(url);
        }
        for(Bundle bundle: bundleContext.getBundles()) {
            url = bundle.getEntry(resource);
            if (url != null && !result.contains(url)) {
                LOG.finest("TAMAYA  Resource: " + resource + " found in registered bundle " + bundle.getSymbolicName());
                result.add(url);
            }
        }
        for(Bundle bundle: bundleContext.getBundles()) {
            url = bundle.getEntry(resource);
            if (url != null && !result.contains(url)) {
                LOG.finest("TAMAYA  Resource: " + resource + " found in unregistered bundle " + bundle.getSymbolicName());
                result.add(url);
            }
        }
        return Collections.enumeration(result);
    }

    public static <T> void registerService(Bundle bundle, Class<T> serviceClass, Class<? extends T> implClass) {
        try {
            // Load the service class
            LOG.info("Loaded Service Factory (" + serviceClass.getName() + "): " + implClass.getName());
            // Provide service properties
            Hashtable<String, String> props = new Hashtable<>();
            props.put(Constants.VERSION_ATTRIBUTE, bundle.getVersion().toString());
            String vendor = bundle.getHeaders().get(Constants.BUNDLE_VENDOR);
            props.put(Constants.SERVICE_VENDOR, (vendor != null ? vendor : "anonymous"));
            // Translate annotated @Priority into a service ranking
            props.put(Constants.SERVICE_RANKING,
                    String.valueOf(PriorityServiceComparator.getPriority(implClass)));

            // Register the service factory on behalf of the intercepted bundle
            JDKUtilServiceFactory factory = new JDKUtilServiceFactory(implClass);
            BundleContext bundleContext = bundle.getBundleContext();
            bundleContext.registerService(serviceClass.getName(), factory, props);
            LOG.info("Registered Tamaya service class: " + implClass.getName() + "(" + serviceClass.getName() + ")");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to load service: " + implClass.getName(), e);
        }
    }

    public static <T> void unregisterService(Bundle bundle, Class<T> serviceClass, Class<? extends T> implClass) {
        try {
            LOG.fine("Unloading Service (" + serviceClass.getName() + "): " + implClass.getName());
            ServiceReference<?> ref = bundle.getBundleContext().getServiceReference(implClass);
            if (ref != null) {
                bundle.getBundleContext().ungetService(ref);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unload service: " + implClass.getName(), e);
        }
    }

    /**
     * Service factory simply instantiating the configured service.
     */
    static class JDKUtilServiceFactory implements ServiceFactory {
        private final Class<?> serviceClass;

        public JDKUtilServiceFactory(Class<?> serviceClass) {
            this.serviceClass = serviceClass;
        }

        @Override
        public Object getService(Bundle bundle, ServiceRegistration registration) {
            try {
                LOG.fine("Creating Service...:" + serviceClass.getName());
                return serviceClass.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IllegalStateException("Failed to create service: " + serviceClass.getName(), ex);
            }
        }

        @Override
        public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        }
    }

}
