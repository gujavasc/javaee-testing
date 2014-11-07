package org.gujavasc.javaee.testing.crud;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public final class Crud<T extends Serializable> implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    private Session session;

    private Class<T> persistentClass;

    private Criteria criteria;

    private Logger log;

    /**
     * init persistentClass based on injection point
     */
    @Inject
    public void init(InjectionPoint ip) {
        if (ip != null && ip.getType() != null) {
            ParameterizedType type = (ParameterizedType) ip.getType();
            Type[] typeArgs = type.getActualTypeArguments();
            try {
                persistentClass = (Class<T>) typeArgs[0];
                log = Logger.getLogger(getClass().getName());
            } catch (ClassCastException e) {
                log.warning("could not find persistent class for " + ip.getType().getClass().getName());
            }
        } else {
            throw new IllegalArgumentException(
                    "Provide entity at injection point ex: @Inject Crud<Entity> crud");
        }
    }

    // example

    public Crud<T> example(T entity) {
        if (entity != null) {
            getCriteria().add(Example.create(entity));
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        return this;
    }

    public Crud<T> example(T entity, List<String> excludeProperties) {
        Example example = null;
        if (entity != null) {
            example = Example.create(entity);
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        if (excludeProperties != null && !excludeProperties.isEmpty()) {
            for (String exclude : excludeProperties) {
                example.excludeProperty(exclude);
            }
        }
        getCriteria().add(example);
        return this;
    }

    public Crud<T> example(T entity, MatchMode mode) {
        if (entity != null) {
            getCriteria().add(Example.create(entity).enableLike(mode));
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        return this;
    }

    public Crud<T> example(T entity, MatchMode mode, List<String> excludeProperties) {
        Example example = null;
        if (entity != null) {
            example = Example.create(entity).enableLike(mode);
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        if (excludeProperties != null && !excludeProperties.isEmpty()) {
            for (String exclude : excludeProperties) {
                example.excludeProperty(exclude);
            }
        }
        getCriteria().add(example);
        return this;
    }

    public Crud<T> maxResult(Integer maxResult) {
        getCriteria().setMaxResults(maxResult);
        return this;
    }

    public Crud<T> firstResult(Integer firstResult) {
        getCriteria().setFirstResult(firstResult);
        return this;
    }

    public Crud<T> criteria(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }

    public Crud<T> criteria() {
        this.criteria = getCriteria();
        return this;
    }

    public Crud projection(Projection projection) {
        getCriteria().setProjection(projection);
        return this;
    }

    //nullsafe restrictions

    public Crud<T> eq(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.eq(property, value));
        }
        return this;
    }

    public Crud<T> ne(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ne(property, value));
        }
        return this;
    }

    public Crud<T> not(Criterion criterion) {
        if (criterion != null) {
            getCriteria().add(Restrictions.not(criterion));
        }
        return this;
    }

    public Crud<T> ilike(String property, String value, MatchMode matchMode) {
        if (value != null) {
            getCriteria().add(Restrictions.ilike(property, value.toString(), matchMode));
        }
        return this;
    }

    public Crud<T> ilike(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ilike(property, value));
        }
        return this;
    }

    public Crud<T> like(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.like(property, value));
        }
        return this;
    }

    public Crud<T> like(String property, String value, MatchMode matchMode) {
        if (value != null) {
            getCriteria().add(Restrictions.like(property, value, matchMode));
        }
        return this;
    }

    public Crud<T> ge(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ge(property, value));
        }
        return this;
    }

    public Crud<T> le(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.le(property, value));
        }
        return this;
    }

    public Crud<T> between(String property, Calendar dtIni, Calendar dtEnd) {
        if (dtIni != null && dtEnd != null) {
            dtIni.set(Calendar.HOUR, 0);
            dtIni.set(Calendar.MINUTE, 0);
            dtIni.set(Calendar.SECOND, 0);
            dtEnd.set(Calendar.HOUR, 23);
            dtEnd.set(Calendar.MINUTE, 59);
            dtEnd.set(Calendar.SECOND, 59);
            getCriteria().add(Restrictions.between(property, dtIni, dtEnd));
        }
        return this;
    }

    public Crud<T> between(String property, Integer ini, Integer end) {
        if (ini != null && end != null) {
            getCriteria().add(Restrictions.between(property, ini, end));
        }
        return this;
    }

    public Crud<T> between(String property, Double ini, Double end) {
        if (ini != null && end != null) {
            getCriteria().add(Restrictions.between(property, ini, end));
        }
        return this;
    }

    public Crud<T> in(String property, List<?> list) {
        if (list != null && !list.isEmpty()) {
            getCriteria().add(Restrictions.in(property, list));
        }
        return this;
    }

    public Crud<T> or(Criterion... criterions) {
        if (criterions != null) {
            getCriteria().add(Restrictions.or(criterions));
        }
        return this;
    }

    public Crud<T> or(Criterion lhs, Criterion rhs) {
        if (lhs != null && rhs != null) {
            getCriteria().add(Restrictions.or(lhs, rhs));
        }
        return this;
    }

    public Crud<T> and(Criterion... criterios) {
        if (criterios != null) {
            getCriteria().add(Restrictions.and(criterios));
        }
        return this;
    }

    public Crud<T> and(Criterion lhs, Criterion rhs) {
        if (lhs != null && rhs != null) {
            getCriteria().add(Restrictions.and(lhs, rhs));
        }
        return this;
    }

    public Crud<T> join(String property, String alias) {
        getCriteria().createAlias(property, alias);
        return this;
    }

    public Crud<T> join(String property, String alias, JoinType type) {
        getCriteria().createAlias(property, alias, type);
        return this;
    }

    public Crud<T> addCriterion(Criterion criterion) {
        if (criterion != null) {
            getCriteria().add(criterion);
        }
        return this;
    }

    public Crud<T> isNull(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNull(property));
        }
        return this;
    }

    public Crud<T> isNotNull(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNotNull(property));
        }
        return this;
    }

    public Crud<T> isEmpty(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isEmpty(property));
        }
        return this;
    }

    public Crud<T> isNotEmpty(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNotEmpty(property));
        }
        return this;
    }

    //retrieve methods

    // find

    /**
     * @return an unique entry from table represented by {@link org.gujavasc.javaee.testing.crud.Crud#persistentClass}
     * based on current com.tdc.poa.crudCrud#criteria and its restrictions
     * @throws javax.persistence.NonUniqueResultException in case current {@link org.gujavasc.javaee.testing.crud.Crud#criteria} doesn't match an unique entry
     */
    public T find() {
        T result = (T) getCriteria().uniqueResult();
        resetCriteria();
        return result;
    }

    // list

    /**
     * @return all entries from the table represented by {@link org.gujavasc.javaee.testing.crud.Crud#persistentClass}
     * based on current {@link org.gujavasc.javaee.testing.crud.Crud#criteria} and its restrictions
     */
    public List<T> list() {
        List<T> result = getCriteria().list();
        resetCriteria();
        return result;
    }

    /**
     * @return all entries from table represented by {@link org.gujavasc.javaee.testing.crud.Crud#persistentClass}
     */
    public List<T> listAll() {
        resetCriteria();
        List<T> result = this.list();
        return result;
    }

    // count

    /**
     * @return number of entries from table represented by {@link org.gujavasc.javaee.testing.crud.Crud#persistentClass}
     * based on current @link{ org.gujavasc.javaee.testing.crud.Crud#criteria} and its restrictions
     */
    public int count() {
        getCriteria().setProjection(Projections.count(getSession()
                .getSessionFactory().getClassMetadata(persistentClass)
                .getIdentifierPropertyName()));
        Long result = (Long) getCriteria().uniqueResult();
        resetCriteria();
        return result.intValue();
    }

    /**
     * @return number of entries from table represented by {@link org.gujavasc.javaee.testing.crud.Crud#persistentClass}
     */
    public int countAll() {
        resetCriteria();
        int result = this.count();
        resetCriteria();
        return result;
    }

    // hibernate session shortcuts

    public T load(Serializable id) {
        return (T) this.getSession().load(persistentClass, id);
    }

    public T get(Serializable id) {
        return (T) this.getSession().get(persistentClass, id);
    }

    public void save(T entity) {
        this.getSession().save(entity);
    }

    public T merge(T entity) {
        return (T) getSession().merge(entity);
    }

    public void update(T entity) {
        this.getSession().update(entity);
    }

    public void delete(T entity) {
        this.getSession().delete(this.get((Serializable) getPK(entity)));
    }

    public T refresh(T entity) {
        this.getSession().refresh(entity);
        return entity;
    }

    public void saveOrUpdate(T entity) {
        this.getSession().saveOrUpdate(entity);
    }

    public Crud<T> addOrderAsc(String property) {
        getCriteria().addOrder(Order.asc(property));
        return this;
    }

    public Crud<T> addOrderDesc(String property) {
        getCriteria().addOrder(Order.desc(property));
        return this;
    }

    private void resetCriteria() {
        criteria = null;
    }

    public void initCriteria() {
        criteria = getSession().createCriteria(getPersistentClass());
    }

    // Getters & Setters

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Session getSession() {
        if (session == null || !session.isOpen()) {
            session = getEntityManager().unwrap(Session.class);
        }
        return session;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Criteria getCriteria() {
        if (criteria == null) {
            criteria = getSession().createCriteria(getPersistentClass());
        }
        return criteria;
    }

    public Criteria getCriteria(boolean reset) {
        Criteria copy = getCriteria();
        if (reset) {
            criteria = null;
        }
        return copy;
    }

    private <PK extends Serializable> PK getPK(T entity) {

        Field idField = getIdField(entity.getClass());
        if (idField == null) {
            throw new RuntimeException("Could not find PK of entity:" + entity.getClass().getName());
        }
        try {
            return (PK) idField.get(entity);
        } catch (IllegalAccessException e) {
            Logger.getLogger(Crud.class.getName()).log(Level.SEVERE, "Could not find PK of entity:" + entity.getClass().getSimpleName(), e);
        }
        //if it gets here no PK was found
        throw new RuntimeException("Could not find PK of entity:" + entity.getClass().getName());
    }

    private Field getIdField(Class clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                f.setAccessible(true);
                return f;
            }
        }
        Class superClass = clazz.getSuperclass();
        if (superClass == null) {
            return null;
        } else {
            return getIdField(superClass);
        }
    }
}