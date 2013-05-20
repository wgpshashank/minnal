/**
 * 
 */
package org.minnal.instrument.entity;

import static org.testng.Assert.assertEquals;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityNodePathTest {
	
	private EntityNode node;
	
	@BeforeMethod
	public void setup() {
		node = new EntityNode(Parent.class);
		node.construct();
	}
	
	@Test
	public void shouldGetBulkUriForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getBulkPath(), "/parents/{parent_id}/children/{child_id}/children");
	}
	
	@Test
	public void shouldGetSingleUriForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getSinglePath(), "/parents/{parent_id}/children/{child_id}/children/{id}");
	}
	
	@Test
	public void shouldGetBulkUriForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getBulkPath(), "/parents");
	}
	
	@Test
	public void shouldGetSingleUriForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getSinglePath(), "/parents/{id}");
	}
	
	@Test
	public void shouldGetSearchParamsForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getSearchParams(), Arrays.asList("code", "id"));
	}
	
	@Test
	public void shouldGetSearchParamsForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child));
		assertEquals(path.getSearchParams(), Arrays.asList("code", "id", "children.code"));
	}
	
	@Test
	public void shouldGetSearchParamsForPathWithAssociations() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getSearchParams(), Arrays.asList("code", "id", "children.code", "children.children.code", "children.children.root.code", "children.children.root.id"));
	}
	
	@Test
	public void shouldGetNameForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getName(), grandChild.getName());
	}
	
	@Test
	public void shouldGetNameForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getName(), node.getName());
	}
	
	@Entity
	private class Parent extends Model {
		@Id
		@Searchable
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@OneToMany
		private Set<Child> children;
		@Override
		public Serializable getId() {
			return id;
		}
	}
	
	@Entity
	private class Child extends Model {
		@Id
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@OneToMany
		private Set<GrandChild> children;
		@Override
		public Serializable getId() {
			return null;
		}
	}
	
	@Entity
	private class GrandChild extends Model {
		@Id
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@ManyToOne
		private Parent root;
		@Override
		public Serializable getId() {
			return null;
		}
	}
}