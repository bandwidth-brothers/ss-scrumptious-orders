package com.ss.scrumptious_orders.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "OWNER")
@Builder
public class RestaurantOwner {

	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@NotBlank
	@Column(nullable = false)
	private String firstName;

	@NotBlank
	@Column(nullable = false)
	private String lastName;

	@NotBlank
	@Column(nullable = false)
	private String phone;

	@NotBlank
	@Column(nullable = false)
	private String email;


}
