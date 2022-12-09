package com.savelms.core.user.domain.repository;

import com.savelms.core.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserCustomRepository {

    private final EntityManager em;
    public List<User> findAllAndSortAndPage(Long offset, Long size) {


        return em.createQuery("SELECT u FROM User u"
                    + " ORDER BY u.nickname",
            User.class)
            .setFirstResult(offset.intValue())
            .setMaxResults(size.intValue())
            .getResultList();
    }
}
