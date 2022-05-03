package edu.java8.practice.service.impl;

import edu.java8.practice.domain.Privilege;
import edu.java8.practice.domain.User;
import edu.java8.practice.service.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        List<User> sortedUsers = users.stream()
                .sorted(Comparator.comparing(f -> f.getFirstName()))
                .sorted((a, b) -> b.getAge().compareTo(a.getAge()))
                .collect(Collectors.toList());
        return sortedUsers;
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        List<List<Privilege>> tempPrivilege = users.stream()
                .map(((User user) -> (user.getPrivileges())))
                .collect(Collectors.toList());

        List<Privilege> distinctPrivilege = tempPrivilege.stream()
                .flatMap(List::stream).distinct().collect(Collectors.toList());
        return distinctPrivilege;
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        Optional<User> updateUser = users.stream()
                .filter((User user) -> (user.getAge() > age))
                .filter((User u) -> (u.getPrivileges().contains(Privilege.UPDATE)))
                .findFirst();

        return updateUser;
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        Map<Integer, List<User>> groupedByNrPrivileges = users.stream()
                .collect(Collectors.groupingBy((p -> p.getPrivileges().size()), Collectors.toList()));

        return groupedByNrPrivileges;
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        double averageAge;
        if (users.isEmpty()) {
            averageAge = -1;
        }else{
            averageAge = users.stream()
                    .collect(Collectors.averagingDouble(User::getAge));
        }

        return averageAge;
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        Optional<String> frequentlyName = users.stream()
                .collect(Collectors.groupingBy(User::getLastName, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .reduce((e1, e2) -> e1.getValue() < e2.getValue()? e2:
                        e1.getValue() > e2.getValue()? e1: new AbstractMap.SimpleImmutableEntry<>(null, e1.getValue()))
                .map(Map.Entry::getKey);

        return frequentlyName;
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        List<User> filteredUsers = users.stream()
                .filter(Arrays.stream(predicates).distinct().reduce(x->true, Predicate::and))
                .collect(Collectors.toList());

        return filteredUsers;
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        String convertedUsers = "";
        convertedUsers = users.stream()
                .map(x -> mapFun.apply(x))
                .collect(Collectors.joining(delimiter));

        return convertedUsers;
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        Map<Privilege, List<User>> groupedByPrivileges = users.stream()
                .flatMap(user -> user.getPrivileges().stream()
                        .map(privilege -> new AbstractMap.SimpleEntry<>(privilege, user)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return groupedByPrivileges;
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        Map<String, Long> numberOfLastName = users.stream()
                .collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));

        return numberOfLastName;
    }
}

