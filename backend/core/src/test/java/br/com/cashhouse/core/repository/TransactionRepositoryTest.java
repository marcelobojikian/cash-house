package br.com.cashhouse.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import com.querydsl.core.types.Predicate;

import br.com.cashhouse.core.model.Dashboard;
import br.com.cashhouse.core.model.Flatmate;
import br.com.cashhouse.core.model.QTransaction;
import br.com.cashhouse.core.model.Transaction.Action;
import br.com.cashhouse.core.model.Transaction.Status;

public class TransactionRepositoryTest {

	QuerydslPredicateArgumentResolver resolver;
	MockHttpServletRequest request;

	@Before
	public void setUp() {
		resolver = new QuerydslPredicateArgumentResolver(new QuerydslBindingsFactory(SimpleEntityPathResolver.INSTANCE), 
				Optional.empty());
		request = new MockHttpServletRequest();
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByIdParameterPredicateCorrectly() throws Exception {

		request.addParameter("id", "1");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.id.eq(1l));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByCreatedParameterPredicateCorrectly() throws Exception {

		request.addParameter("createBy.id", "1");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.createBy.id.eq(1l));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByAssignedParameterPredicateCorrectly() throws Exception {

		request.addParameter("assigned.id", "1");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.assigned.id.eq(1l));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByCashierParameterPredicateCorrectly() throws Exception {

		request.addParameter("cashier.id", "100");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.cashier.id.eq(100l));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByStatusParameterPredicateCorrectly() throws Exception {

		request.addParameter("status", "CREATED");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.status.eq(Status.CREATED));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByActionParameterPredicateCorrectly() throws Exception {

		request.addParameter("action", "DEPOSIT");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.action.eq(Action.DEPOSIT));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByValueParameterPredicateCorrectly() throws Exception {

		request.addParameter("value", "32.42");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(QTransaction.transaction.value.eq(BigDecimal.valueOf(32.42d)));
		
	}

	@Test
	public void resolveArgumentShouldCreateTransactionByAllParameterPredicateCorrectly() throws Exception {

		request.addParameter("id", "1");
		request.addParameter("createBy.id", "1");
		request.addParameter("assigned.id", "1");
		request.addParameter("cashier.id", "100");
		request.addParameter("status", "CREATED");
		request.addParameter("action", "DEPOSIT");
		request.addParameter("value", "32.42");

		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
				new ServletWebRequest(request), null);

		assertThat(predicate).isEqualTo(
				QTransaction.transaction.id.eq(1l)
				.and(QTransaction.transaction.createBy.id.eq(1l))
				.and(QTransaction.transaction.assigned.id.eq(1l))
				.and(QTransaction.transaction.cashier.id.eq(100l))
				.and(QTransaction.transaction.status.eq(Status.CREATED))
				.and(QTransaction.transaction.action.eq(Action.DEPOSIT))
				.and(QTransaction.transaction.value.eq(BigDecimal.valueOf(32.42d))));
	}

	private static MethodParameter getMethodParameterFor(String methodName, Class<?>... args) throws RuntimeException {

		try {
			return new MethodParameter(TransactionRepository.class.getMethod(methodName, args), args.length == 0 ? -1 : 0);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		
	}

//	@Test
//	public void resolveArgumentShouldCreateTransactionByCreatedDateParameterPredicateCorrectly() throws Exception {
//
//		request.addParameter("createdDate", "2020-12-21T10:23:40");
//
//		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
//				new ServletWebRequest(request), null);
//		
//
//		BooleanExpression eq = QTransaction.transaction.createdDate.eq(LocalDateTime.of(2020, 12, 21, 10, 23, 40));
//		
//		assertThat(predicate).isEqualTo(eq);
//		
//	}

//	@Test
//	public void resolveArgumentShouldCreateTransactionByUpdatedDateParameterPredicateCorrectly() throws Exception {
//
//		request.addParameter("updatedDate", "2018-10-22");
//
//		Object predicate = resolver.resolveArgument(getMethodParameterFor("findAll", Flatmate.class , Dashboard.class, Predicate.class, Pageable.class), null,
//				new ServletWebRequest(request), null);
//
//		assertThat(predicate).isEqualTo(QTransaction.transaction.updatedDate.eq(LocalDateTime.now()));
//		
//	}

}
