package com.zafin.zplatform.proto;

import org.springframework.context.ApplicationContext;

import com.zafin.zplatform.proto.service.StartupArgs;

public class SpringServiceRegistryEntry<T,B> implements ServiceRegistry<T, B> {
    
    private final RegistryKey<T,B> registryKey;
    
    private SpringServiceRegistryEntry(Builder<T,B> builder) {
       this.registryKey = new RegistryKey<T,B>(builder.getSpringConfigClass(),
               builder.getSpringApplicationClass(),
               builder.getRevision(),
               builder.getArgs(),
               builder.getPort());
    }
    public int port() {
        return registryKey.port;
    }
    
    @SuppressWarnings("hiding")
    public class RegistryKey<T,B> {
        private final String springConfigClass;
        private final String springApplicationClass;
        private final int revision;
        private final StartupArgs args;
        private final int port;
        
        private ApplicationContext context;
        
        private RegistryKey(String springConfig,
                String springApplicationClass,
                int revision,
                StartupArgs args,
                int port) {
            this.springConfigClass = springConfig;
            this.springApplicationClass = springApplicationClass;
            this.revision = revision;
            this.args = args;
            this.port = port;
        }
        
        @Override
        public String toString() {
            return "ConfigClass: " + springConfigClass + ", Revision: " + revision + ", Starup Args: "+args.toString()+", Port: "+port;
        }

        public String getSpringConfig() {
            return springConfigClass;
        }

        public int getRevision() {
            return revision;
        }

        public StartupArgs getStartupArgs() {
            return args;
        }
        
        public String[] args() {
            return args.get();
        }

        public int getPort() {
            return port;
        }

        public ApplicationContext getContext() {
            return context;
        }

        public void setContext(ApplicationContext context) {
            this.context = context;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((args == null) ? 0 : args.hashCode());
            result = prime * result + ((context == null) ? 0 : context.hashCode());
            result = prime * result + port;
            result = prime * result + revision;
            result = prime * result + ((springConfigClass == null) ? 0 : springConfigClass.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            @SuppressWarnings("unchecked")
            RegistryKey<?,?> other = (RegistryKey<?,?>) obj;
            if (args == null) {
                if (other.args != null)
                    return false;
            } else if (!args.equals(other.args))
                return false;
            if (context == null) {
                if (other.context != null)
                    return false;
            } else if (!context.equals(other.context))
                return false;
            if (port != other.port)
                return false;
            if (revision != other.revision)
                return false;
            if (springConfigClass == null) {
                if (other.springConfigClass != null)
                    return false;
            } else if (!springConfigClass.equals(other.springConfigClass))
                return false;
            return true;
        }

        public String getSpringApplicationClass() {
            return springApplicationClass;
        }
    }
    
//    public ApplicationContext run() throws ClassNotFoundException {
//        System.out.println(this.getClass().getCanonicalName() + ".run()...");
//        Class<?> application = Class.forName(registryKey.getSpringApplicationClass());
//        Class<?> config = Class.forName(registryKey.getSpringConfig());
//        return SpringApplication.run(config, registryKey.args());
//        
//    }

    /* (non-Javadoc)
     * @see com.zafin.zplatform.proto.ServiceRegistry#getBean(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Client<T,B> getBean(String name) {
        Client<T,B> client = null;
        ApplicationContext context = registryKey.getContext();
        if (context == null) {
            throw new IllegalStateException("No context registered for revision [" + registryKey.getRevision() + "]");
        } else {
            System.out.println("Context found with registry key: " + registryKey);
        }
        client = (Client<T,B>) context.getBean(name, Client.class);
        if (client == null) {
            throw new IllegalStateException("No Spring bean with name: [" + name + "] configured for config with revision [" + registryKey.getRevision() + "]. RegistryKey used: " + registryKey);
        }
        return client;
    }
    
    @SuppressWarnings("rawtypes")
    public static Builder builder() {
        return new Builder<>();
    }
    
    public static class Builder<T,B> {
        private String springConfigClass;
        private StartupArgs args;
        private String springApplicationClass;
        
        public int getPort() {
            return 8080 + getRevision();
        }
       
        private int getRevision() {
            return RevisionUtil.getRevisionFromClassName(springConfigClass);
        }
        public Builder<T,B> setSpringConfig(String springConfig) {
            this.springConfigClass = springConfig;
            return this;
        }
       
        public Builder<T,B> setArgs(StartupArgs args) {
            this.args = args;
            return this;
        }
        
        public Builder<T,B> setSpringApplicationClass(String springApplicationClass) {
            this.springApplicationClass = springApplicationClass;
            return this;
        }
        
        public SpringServiceRegistryEntry<T,B> build() {
            if (springConfigClass == null) {
                throw new IllegalStateException("SpringConfig is null.");
            }
            return new SpringServiceRegistryEntry<>(this);
        }

        public String getSpringConfigClass() {
            return springConfigClass;
        }

        public StartupArgs getArgs() {
            return args;
        }

        public String getSpringApplicationClass() {
            return springApplicationClass;
        }
        
    }

    @Override
    public Object getRegistryKey() {
        return registryKey;
    }
}
