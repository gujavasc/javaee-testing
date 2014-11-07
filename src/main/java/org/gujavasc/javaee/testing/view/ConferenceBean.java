package org.gujavasc.javaee.testing.view;

import org.gujavasc.javaee.testing.model.Conference;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Stateful
@ConversationScoped
public class ConferenceBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Conference entities
    */

    private Long id;
    private Conference conference;
    @Inject
    private Conversation conversation;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Conference> pageItems;
    private Conference example = new Conference();
    @Resource
    private SessionContext sessionContext;
    private Conference add = new Conference();

   /*
    * Support updating and deleting Conference entities
    */

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /*
    * Support searching Conference entities with pagination
    */

    public Conference getConference() {
        return this.conference;
    }

    public String create() {

        this.conversation.begin();
        return "create?faces-redirect=true";
    }

    public void retrieve() {

        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        if (this.conversation.isTransient()) {
            this.conversation.begin();
        }

        if (this.id == null) {
            this.conference = this.example;
        } else {
            this.conference = findById(getId());
        }
    }

    public Conference findById(Long id) {

        return this.entityManager.find(Conference.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.conference);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.conference);
                return "view?faces-redirect=true&id=" + this.conference.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            Conference deletableEntity = findById(getId());

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public Conference getExample() {
        return this.example;
    }

    public void setExample(Conference example) {
        this.example = example;
    }

    public void search() {
        this.page = 0;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Conference> root = countCriteria.from(Conference.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Conference> criteria = builder.createQuery(Conference.class);
        root = criteria.from(Conference.class);
        TypedQuery<Conference> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Conference> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String name = this.example.getName();
        if (name != null && !"".equals(name)) {
            predicatesList.add(builder.like(root.<String>get("name"), '%' + name + '%'));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

   /*
    * Support listing and POSTing back Conference entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

    public List<Conference> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Conference> getAll() {

        CriteriaQuery<Conference> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(Conference.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Conference.class))).getResultList();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final ConferenceBean ejbProxy = this.sessionContext.getBusinessObject(ConferenceBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                                      UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context,
                                      UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((Conference) value).getId());
            }
        };
    }

    public Conference getAdd() {
        return this.add;
    }

    public Conference getAdded() {
        Conference added = this.add;
        this.add = new Conference();
        return added;
    }
}