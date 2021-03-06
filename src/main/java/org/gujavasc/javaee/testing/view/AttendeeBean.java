package org.gujavasc.javaee.testing.view;

import org.gujavasc.javaee.testing.model.Attendee;

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
public class AttendeeBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Attendee entities
    */

    private Long id;
    private Attendee attendee;
    @Inject
    private Conversation conversation;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Attendee> pageItems;
    private Attendee example = new Attendee();
    @Resource
    private SessionContext sessionContext;
    private Attendee add = new Attendee();

   /*
    * Support updating and deleting Attendee entities
    */

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /*
    * Support searching Attendee entities with pagination
    */

    public Attendee getAttendee() {
        return this.attendee;
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
            this.attendee = this.example;
        } else {
            this.attendee = findById(getId());
        }
    }

    public Attendee findById(Long id) {

        return this.entityManager.find(Attendee.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.attendee);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.attendee);
                return "view?faces-redirect=true&id=" + this.attendee.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            Attendee deletableEntity = findById(getId());

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

    public Attendee getExample() {
        return this.example;
    }

    public void setExample(Attendee example) {
        this.example = example;
    }

    public void search() {
        this.page = 0;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Attendee> root = countCriteria.from(Attendee.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Attendee> criteria = builder.createQuery(Attendee.class);
        root = criteria.from(Attendee.class);
        TypedQuery<Attendee> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Attendee> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String name = this.example.getName();
        if (name != null && !"".equals(name)) {
            predicatesList.add(builder.like(root.<String>get("name"), '%' + name + '%'));
        }
        int age = this.example.getAge();
        if (age != 0) {
            predicatesList.add(builder.equal(root.get("age"), age));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

   /*
    * Support listing and POSTing back Attendee entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

    public List<Attendee> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Attendee> getAll() {

        CriteriaQuery<Attendee> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(Attendee.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Attendee.class))).getResultList();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final AttendeeBean ejbProxy = this.sessionContext.getBusinessObject(AttendeeBean.class);

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

                return String.valueOf(((Attendee) value).getId());
            }
        };
    }

    public Attendee getAdd() {
        return this.add;
    }

    public Attendee getAdded() {
        Attendee added = this.add;
        this.add = new Attendee();
        return added;
    }
}