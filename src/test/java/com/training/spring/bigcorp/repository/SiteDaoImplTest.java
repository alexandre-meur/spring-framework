package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class SiteDaoImplTest {

    @Autowired
    private SiteDao siteDao;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void findById() {
        Optional<Site> site = siteDao.findById("site1");
        Assertions.assertThat(site.get().getName()).isEqualTo("Bigcorp Lyon");
        Assertions.assertThat(site.get().getVersion()).isEqualTo(0);
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Site> site = siteDao.findById("unknown");
        Assertions.assertThat(site).isEmpty();
    }

    @Test
    public void findAll() {
        List<Site> sites = siteDao.findAll();
        Assertions.assertThat(sites)
                .hasSize(1)
                .extracting("id", "name")
                .contains(Tuple.tuple("site1", "Bigcorp Lyon"));
    }

    @Test
    public void create() {
        Site site = new Site("New site");
        site.setVersion(1);
        Assertions.assertThat(siteDao.findAll()).hasSize(1);
        siteDao.save(site);
        Assertions.assertThat(siteDao.findAll())
                .hasSize(2)
                .extracting(Site::getName)
                .contains("Bigcorp Lyon", "New site");
    }

    @Test
    public void update() {
        Optional<Site> site = siteDao.findById("site1");
        Assertions.assertThat(site.get().getName()).isEqualTo("Bigcorp Lyon");
        Assertions.assertThat(site.get().getVersion()).isEqualTo(0);
                site.ifPresent(s -> {
                    s.setName("Site updated");
                    s.setVersion(1);
                    siteDao.save(s);
                });
        site = siteDao.findById("site1");
        Assertions.assertThat(site.get().getName()).isEqualTo("Site updated");
        Assertions.assertThat(site.get().getVersion()).isEqualTo(1);
    }

    @Test
    public void deleteById() {
        Site newsite = new Site("New site");
        siteDao.save(newsite);
        Assertions.assertThat(siteDao.findById(newsite.getId())).isNotEmpty();
        siteDao.delete(newsite);
        Assertions.assertThat(siteDao.findById(newsite.getId())).isEmpty();
    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Site site = siteDao.getOne("site1");
        Assertions
                .assertThatThrownBy(() -> {
                    siteDao.delete(site);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }

        //Test d'accès concurenciel
    @Test
    public void preventConcurrentWrite() {
        Site site = siteDao.getOne("site1");

        // A la base le numéro de version est à sa valeur initiale
        Assertions.assertThat(site.getVersion()).isEqualTo(0);

        // On detache cet objet du contexte de persistence
        entityManager.detach(site);
        site.setName("Site updated");

        // On force la mise à jour en base (via le flush) et on vérifie que l'objet retourné
        // est attaché à la session a été mis à jour
        Site attachedCaptor = siteDao.save(site);
        entityManager.flush();
        Assertions.assertThat(attachedCaptor.getName()).isEqualTo("Site updated");
        Assertions.assertThat(attachedCaptor.getVersion()).isEqualTo(1);

        // Si maintenant je réessaie d'enregistrer captor, comme le numéro de version est
        // à 0 je dois avoir une exception
        Assertions.assertThatThrownBy(() -> siteDao.save(site))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

        //Tests de validation
    @Test
    public void createShouldThrowExceptionWhenNameIsNull() {
        Assertions
                .assertThatThrownBy(() -> {
                    siteDao.save(new Site(null));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("ne peut pas être nul");
    }
    @Test
    public void createShouldThrowExceptionWhenNameSizeIsInvalid() {
        Assertions
                .assertThatThrownBy(() -> {
                    siteDao.save(new Site("ee"));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("la taille doit être comprise entre 3 et 100");
    }
}

